package com.arffy.server.infra.s3.service

import com.amazonaws.services.s3.model.S3Object
import com.arffy.server.global.exception.GlobalErrorCode
import com.arffy.server.global.exception.RestApiException
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.dto.ImageDto
import com.arffy.server.infra.s3.entity.ImageEntity
import com.arffy.server.infra.s3.exception.ImageDeleteException
import com.arffy.server.infra.s3.exception.ImageErrorCode
import com.arffy.server.infra.s3.exception.ImageSaveException
import com.arffy.server.infra.s3.exception.ImageServiceException
import com.arffy.server.infra.s3.repository.ImageRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


private val log = KotlinLogging.logger {}

@Service
class ImageServiceImpl(
    private val imageRepository: ImageRepository,
    private val s3Service: S3Service,
) : ImageService {
    @Transactional
    override fun save(
        entityId: Long, multipartFiles: List<MultipartFile>?, saveImageCount: Int,
        deleteImageCount: Int, imageType: ImageType
    ) {
        if (multipartFiles.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.NOT_FOUND_IMAGE)
        }
        s3Service.checkImageCount(
            multipartFiles, saveImageCount, deleteImageCount, imageType
        )
        s3Service.check(multipartFiles)
        var imageList: List<String> = ArrayList()
        try {
            imageList = s3Service.save(multipartFiles, imageType)
        } catch (e: ImageServiceException) {
            log.error("thumbnail upload 실패")
            log.error("error : ", e)
            throw ImageSaveException(e.imageServiceErrorReason, e.message ?: "thumbnail upload 실패")
        }

        if (imageList.isEmpty()) {
            throw RestApiException(ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE)
        }

        try {
            val imageEntities: MutableList<ImageEntity> = ArrayList()
            imageList.forEach { image ->
                imageEntities.add(
                    ImageEntity(
                        divideId = entityId,
                        imageType = imageType,
                        imageUrl = image
                    )
                )
            }
            imageRepository.saveAll(imageEntities)
        } catch (e: RuntimeException) {
            log.error("image rollback 실패")
            log.error("error : ", e)
            for (url in imageList) {
                s3Service.delete(url)
            }
            throw RestApiException(GlobalErrorCode.INTERNAL_SERVER_GLOBAL_ERROR)
        }
    }

    override fun save(entityId: Long, multipartFiles: List<MultipartFile>, imageType: ImageType): List<String> {
        return try {
            s3Service.save(multipartFiles, imageType)
        } catch (e: ImageServiceException) {
            log.error("thumbnail upload 실패")
            log.error("error : ", e)
            throw ImageSaveException(e.imageServiceErrorReason, e.message ?: "thumbnail upload 실패")
        }
    }

    override fun save(multipartFiles: List<MultipartFile>?, keyPaths: List<String>?): List<String> {
        if (multipartFiles.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.REQUIRE_IMAGE)
        }
        if (keyPaths.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.REQUIRE_IMAGE_URL)
        }
        if (multipartFiles.size != keyPaths.size) {
            throw RestApiException(ImageErrorCode.NOT_EQUAL_IMAGE_AND_IMAGE_URL_QUANTITY)
        }
        try {
            val imageUrl: MutableList<String> = java.util.ArrayList()
            for (i in multipartFiles.indices) {
                imageUrl.add(s3Service.save(keyPaths[i], multipartFiles[i]))
            }
            return imageUrl
        } catch (e: ImageServiceException) {
            log.error("thumbnail upload 실패")
            log.error("error : ", e)
            throw ImageSaveException(e.imageServiceErrorReason, e.message ?: "thumbnail upload 실패")
        }

    }

    @Transactional
    override fun rollbackSave(divideId: Long, s3Objects: List<S3Object>, imageType: ImageType) {
        try {
            val imageList: MutableList<String> = ArrayList()
            for (s3Object in s3Objects) {
                imageList.add(s3Service.save(s3Object))
            }
            val imageEntities: MutableList<ImageEntity> = ArrayList()
            imageList.forEach { image: String ->
                imageEntities.add(
                    ImageEntity(
                        divideId = divideId,
                        imageType = imageType,
                        imageUrl = image
                    )
                )
            }
            imageRepository.saveAll(imageEntities)
        } catch (e: RestApiException) {
            log.error("image rollback 실패")
            log.error("error : ", e)
            throw RestApiException(GlobalErrorCode.INTERNAL_SERVER_GLOBAL_ERROR)
        }
    }

    @Transactional
    override fun delete(deleteImages: List<Long>?, s3Objects: List<S3Object>) {
        if (deleteImages.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.NOT_FOUND_IMAGE)
        }
        val imageEntities = getImageEntities(deleteImages)
        if (imageEntities.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.NOT_FOUND_IMAGE)
        }
        val urls = imageEntities.map { it.imageUrl }
        try {
            s3Service.delete(urls, s3Objects)
        } catch (e: ImageServiceException) {
            log.error("image delete 실패")
            log.error("error : ", e)
            throw ImageDeleteException(e.imageServiceErrorReason, e.errorMessage)
        }
        try {
            imageRepository.deleteAll(imageEntities)
        } catch (e: RuntimeException) {
            log.error("image delete 실패")
            log.error("error : ", e)
            for (s3Object in s3Objects) {
                s3Service.save(s3Object)
            }
            throw RestApiException(GlobalErrorCode.INTERNAL_SERVER_GLOBAL_ERROR)
        }
    }

    override fun getS3Object(imageIds: List<Long>?): List<S3Object> {
        if (imageIds.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.NOT_FOUND_IMAGE)
        }
        val imageEntities = getImageEntities(imageIds)
        if (imageEntities.isNullOrEmpty()) {
            throw RestApiException(ImageErrorCode.NOT_FOUND_OBJECT)
        }
        val keyPaths = imageEntities.map { it.imageUrl }
        return try {
            s3Service.getObject(keyPaths)
        } catch (e: RuntimeException) {
            log.error("get s3Object image 실패")
            log.error("error : ", e)
            throw RestApiException(GlobalErrorCode.INTERNAL_SERVER_GLOBAL_ERROR)
        }
    }

    override fun getImageList(divideId: Long, imageType: ImageType): List<ImageDto> {
        return imageRepository.findByImageTypeAndDivideIdOrderByIdAsc(imageType, divideId)?.map { ImageDto.from(it) }
            ?: return emptyList()
    }

    @Transactional
    override fun deletes(divideId: Long?, imageType: ImageType) {
        if (divideId == null) {
            throw RestApiException(ImageErrorCode.REQUIRE_IMAGE_ID)
        }
        val imageEntities = imageRepository.findByImageTypeAndDivideIdOrderByIdAsc(
            imageType,
            divideId
        )
        if (imageEntities.isNullOrEmpty()) {
            return
        }
        try {
            s3Service.deletes(
                imageEntities.map { it.imageUrl }
            )
        } catch (e: ImageServiceException) {
            log.error("공지사항 삭제 시 aws s3 image delete 실패")
            log.error("error : ", e)
        }
        try {
            imageRepository.deleteAll(imageEntities)
        } catch (e: RuntimeException) {
            log.error("공지사항 삭제 시 db image delete 실패")
            log.error("error : ", e)
        }
    }

    override fun deleteIsLatest(keyPath: String?) {
        try {
            s3Service.deleteIsLatest(keyPath)
        } catch (e: ImageServiceException) {
            log.error("image delete 실패")
            log.error("error : ", e)
            throw ImageDeleteException(e.imageServiceErrorReason, e.errorMessage)
        }
    }

    private fun getImageEntities(imageIds: List<Long>): List<ImageEntity>? {
        return imageRepository.findByIdIn(imageIds)
    }
}
