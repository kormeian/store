package com.arffy.server.infra.s3.service

import com.amazonaws.services.s3.model.S3Object
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.dto.ImageDto
import org.springframework.web.multipart.MultipartFile

interface ImageService {
    fun save(
        entityId: Long, multipartFiles: List<MultipartFile>?, saveImageCount: Int,
        deleteImageCount: Int, imageType: ImageType
    )

    fun save(entityId: Long, multipartFiles: List<MultipartFile>, imageType: ImageType): List<String>
    fun save(multipartFiles: List<MultipartFile>?, keyPaths: List<String>?): List<String>
    fun rollbackSave(divideId: Long, s3Objects: List<S3Object>, imageType: ImageType)
    fun delete(deleteImages: List<Long>?, s3Objects: List<S3Object>)
    fun getS3Object(imageIds: List<Long>?): List<S3Object>
    fun getImageList(divideId: Long, imageType: ImageType): List<ImageDto>
    fun deletes(divideId: Long?, imageType: ImageType)
    fun deleteIsLatest(keyPath: String?)
}
