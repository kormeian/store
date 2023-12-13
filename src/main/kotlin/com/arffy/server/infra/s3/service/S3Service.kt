package com.arffy.server.infra.s3.service

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import com.arffy.server.infra.s3.constant.ImageType
import com.arffy.server.infra.s3.exception.ImageErrorCode
import com.arffy.server.infra.s3.exception.ImageServiceException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.ObjectUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*


private val log = KotlinLogging.logger {}

@Service
class S3Service(
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    @Value("\${cloud.aws.baseUrl}")
    private val baseUrl: String,
) {
    fun save(multipartFile: MultipartFile?, imageType: ImageType?): String {
        if (imageType == null || ObjectUtils.isEmpty(imageType)) {
            throw ImageServiceException(
                ImageErrorCode.REQUIRE_IMAGE_TYPE
            )
        }
        if (multipartFile == null || multipartFile.isEmpty) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        val fileName = createFileName(multipartFile.originalFilename ?: "dummy file name")
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentLength = multipartFile.size
        objectMetadata.contentType = multipartFile.contentType
        val keyPath: String = imageType.name.lowercase(Locale.getDefault()) + "/" + fileName
        try {
            multipartFile.inputStream.use { inputStream ->
                amazonS3.putObject(
                    PutObjectRequest(bucket, keyPath, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                )
            }
        } catch (e: IOException) {
            log.error("putObject multipartFile : " + multipartFile.originalFilename)
            throw ImageServiceException(
                ImageErrorCode.FAILURE_UPLOAD_IMAGE
            )
        } catch (e: SdkClientException) {
            log.error("putObject multipartFile : " + multipartFile.originalFilename)
            throw ImageServiceException(
                ImageErrorCode.FAILURE_UPLOAD_IMAGE
            )
        }
        return baseUrl + keyPath
    }

    fun save(s3Object: S3Object): String {
        if (ObjectUtils.isEmpty(s3Object)
            || ObjectUtils.isEmpty(s3Object.objectContent)
            || ObjectUtils.isEmpty(s3Object.key)
        ) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        try {
            amazonS3.putObject(
                PutObjectRequest(bucket, s3Object.key, s3Object.objectContent, s3Object.objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
            )
        } catch (e: SdkClientException) {
            throw ImageServiceException(
                ImageErrorCode.FAILURE_UPLOAD_IMAGE
            )
        }
        return baseUrl + s3Object.key
    }

    fun save(keyPath: String, multipartFile: MultipartFile?): String {
        if (multipartFile == null || multipartFile.isEmpty) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        try {
            val objectMetadata = ObjectMetadata()
            objectMetadata.contentLength = multipartFile.size
            objectMetadata.contentType = multipartFile.contentType
            multipartFile.inputStream.use { inputStream ->
                amazonS3.putObject(
                    PutObjectRequest(bucket, keyPath, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                )
            }
        } catch (e: IOException) {
            log.error("putObject multipartFile : " + multipartFile.originalFilename)
            throw ImageServiceException(
                ImageErrorCode.FAILURE_UPLOAD_IMAGE
            )
        } catch (e: SdkClientException) {
            throw ImageServiceException(
                ImageErrorCode.FAILURE_UPLOAD_IMAGE
            )
        }
        return baseUrl + keyPath
    }

    fun check(multipartFile: MultipartFile?) {
        if (multipartFile == null || multipartFile.isEmpty) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        val filename = multipartFile.originalFilename
        if (filename != null) {
            checkName(filename)
        } else {
            log.error("check multipartFile : " + multipartFile.originalFilename)
            throw ImageServiceException(
                ImageErrorCode.MISMATCH_FILE_TYPE
            )
        }
    }

    fun save(multipartFiles: List<MultipartFile>?, imageType: ImageType): List<String> {
        if (multipartFiles.isNullOrEmpty()) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        val urls: MutableList<String> = ArrayList()
        return try {
            for (multipartFile in multipartFiles) {
                urls.add(this.save(multipartFile, imageType))
            }
            log.info("save List<multipartFile> size : " + multipartFiles.size)
            urls
        } catch (e: ImageServiceException) {
            log.error("aws s3 이미지 저장 실패, 저장된 이미지 삭제 진행....")
            if (!ObjectUtils.isEmpty(urls)) {
                for (url in urls) {
                    this.delete(url)
                }
            }
            throw e
        }
    }

    fun save(
        multipartFiles: List<MultipartFile>?, imageType: ImageType, keyPaths: List<String>
    ): List<String>? {
        if (multipartFiles.isNullOrEmpty()) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        val urls: MutableList<String> = ArrayList()
        for (multipartFile in multipartFiles) {
            urls.add(save(multipartFile, imageType))
        }
        log.info("save List<multipartFile> size : " + multipartFiles.size)
        return urls
    }

    fun check(multipartFiles: List<MultipartFile>?) {
        if (multipartFiles.isNullOrEmpty()) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        for (multipartFile in multipartFiles) {
            check(multipartFile)
        }
        log.info("check List<multipartFile> size : " + multipartFiles.size)
    }

    fun checkImageCount(
        multipartFiles: List<MultipartFile>?, saveImageCount: Int,
        deleteImageCount: Int, imageType: ImageType
    ) {
        if (multipartFiles.isNullOrEmpty()) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
            )
        }
        multipartFiles.forEach { multipartFile: MultipartFile ->
            if (multipartFile.isEmpty) {
                throw ImageServiceException(
                    ImageErrorCode.NOT_FOUND_UPLOAD_IMAGE
                )
            }
        }
        val multipartFiesSize = multipartFiles.size
        if (multipartFiesSize + saveImageCount - deleteImageCount > imageType.imageQuantity) {
            log.error("checkImageCount totalCount : $multipartFiesSize$saveImageCount")
            throw ImageServiceException(
                ImageErrorCode.TOO_MANY_UPLOAD_IMAGE,
                String.format(
                    ImageErrorCode.TOO_MANY_UPLOAD_IMAGE.message,
                    saveImageCount, multipartFiesSize, deleteImageCount,
                    imageType.name, imageType.imageQuantity
                )
            )
        }
    }

    fun delete(key: String): Boolean {
        try {
            amazonS3.deleteObject(DeleteObjectRequest(bucket, key.replace(baseUrl.toRegex(), "")))
        } catch (e: SdkClientException) {
            log.error("delete url : $key")
            throw ImageServiceException(
                ImageErrorCode.FAILURE_DELETE_IMAGE
            )
        }
        return true
    }

    fun delete(urls: List<String>, s3Objects: List<S3Object>?) {
        if (s3Objects.isNullOrEmpty()) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_DELETE_IMAGE
            )
        }
        val deleteUrls: MutableList<S3Object> = ArrayList()
        val s3ObjectMap = HashMap<String, S3Object>()
        for (s3Object in s3Objects) {
            s3ObjectMap[s3Object.key] = s3Object
        }
        try {
            for (url in urls) {
                this.delete(url)
                deleteUrls.add(s3ObjectMap[url.replace(baseUrl.toRegex(), "")] ?: continue)
            }
        } catch (e: ImageServiceException) {
            log.error("aws s3 이미지 삭제 실패, 삭제된 이미지 저장 진행....")
            if (deleteUrls.isNotEmpty()) {
                for (s3Object in deleteUrls) {
                    this.save(s3Object)
                }
            }
            throw e
        }
    }

    fun deletes(paths: List<String>?) {
        if (paths.isNullOrEmpty()) {
            return
        }
        try {
            val keyVersions: MutableList<DeleteObjectsRequest.KeyVersion> = ArrayList()
            for (path in paths) {
                keyVersions.add(DeleteObjectsRequest.KeyVersion(path.replace(baseUrl.toRegex(), "")))
            }
            val deleteObjectsRequest = DeleteObjectsRequest(bucket).withKeys(keyVersions).withQuiet(false)
            val response = amazonS3.deleteObjects(deleteObjectsRequest)
        } catch (e: SdkClientException) {
            throw ImageServiceException(
                ImageErrorCode.FAILURE_DELETE_IMAGE
            )
        }
    }

    fun deleteIsLatest(keyPath: String?) {
        if (keyPath.isNullOrBlank()) {
            return
        }
        try {
            val request = ListVersionsRequest()
                .withBucketName(bucket)
                .withPrefix(keyPath.replace(baseUrl.toRegex(), ""))
            val versionListing = amazonS3.listVersions(request)
            val versionSummaries = versionListing.versionSummaries
            var key: String? = null
            var versionId: String? = null
            for (s3VersionSummary in versionSummaries) {
                if (s3VersionSummary.isLatest) {
                    key = s3VersionSummary.key
                    versionId = s3VersionSummary.versionId
                }
            }
            amazonS3.deleteVersion(bucket, key, versionId)
        } catch (e: SdkClientException) {
            throw ImageServiceException(
                ImageErrorCode.FAILURE_DELETE_IMAGE
            )
        }
    }

    fun getObject(keyPaths: List<String>?): List<S3Object> {
        if (keyPaths.isNullOrEmpty()) {
            throw ImageServiceException(
                ImageErrorCode.NOT_FOUND_GET_IMAGE
            )
        }
        val objects: MutableList<S3Object> = ArrayList()
        try {
            for (i in keyPaths.indices) {
                objects.add(amazonS3.getObject(bucket, keyPaths[i].replace(baseUrl.toRegex(), "")))
            }
        } catch (e: SdkClientException) {
            throw ImageServiceException(
                ImageErrorCode.FAILURE_GET_IMAGE
            )
        }
        return objects
    }

    private fun checkName(fileName: String) {
        log.info("checkName fileName : $fileName")
        try {
            val extension = fileName.substring(fileName.lastIndexOf(".")).lowercase(Locale.getDefault())
            if (extension == ".png" || extension == ".jpg" || extension == ".jpeg") {
                return
            }
            log.error("checkName fileName : $fileName")
            throw ImageServiceException(
                ImageErrorCode.INVALID_FORMAT_FILE
            )
        } catch (e: StringIndexOutOfBoundsException) {
            log.error("checkName fileName : $fileName")
            throw ImageServiceException(
                ImageErrorCode.MISMATCH_FILE_TYPE
            )
        }
    }

    private fun createFileName(fileName: String): String {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + getFileExtension(fileName)
    }

    private fun getFileExtension(fileName: String): String {
        log.info("getFileExtension fileName : $fileName")
        try {
            val extension = fileName.substring(fileName.lastIndexOf(".")).lowercase(Locale.getDefault())
            if (extension == ".png" || extension == ".jpg" || extension == ".jpeg") {
                return extension
            }
            log.error("getFileExtension fileName : $fileName")
            throw ImageServiceException(
                ImageErrorCode.INVALID_FORMAT_FILE
            )
        } catch (e: StringIndexOutOfBoundsException) {
            log.error("getFileExtension fileName : $fileName")
            throw ImageServiceException(
                ImageErrorCode.MISMATCH_FILE_TYPE
            )
        }
    }
}