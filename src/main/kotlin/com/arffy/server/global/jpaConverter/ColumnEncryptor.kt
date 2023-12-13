package com.arffy.server.global.jpaConverter

import com.arffy.server.global.exception.GlobalErrorCode
import com.arffy.server.global.exception.RestApiException
import org.apache.commons.codec.binary.Hex
import org.springframework.beans.factory.annotation.Value
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter
import javax.persistence.Converter


@Converter
class ColumnEncryptor : AttributeConverter<String, String> {

    @Value("\${spring.database.column.encrypt.key}")
    private lateinit var key: String
    private lateinit var encryptCipher: Cipher
    private lateinit var decryptCipher: Cipher

    @PostConstruct
    fun init() {
        encryptCipher = Cipher.getInstance("AES")
        encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(key, "UTF-8"))
        decryptCipher = Cipher.getInstance("AES")
        decryptCipher.init(Cipher.DECRYPT_MODE, generateMySQLAESKey(key, "UTF-8"))
    }

    override fun convertToDatabaseColumn(attribute: String?): String? {
        if (attribute == null) return null
        try {
            return String(Hex.encodeHex(encryptCipher.doFinal(attribute.toByteArray(StandardCharsets.UTF_8)))).uppercase(
                Locale.getDefault()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) return null
        try {
            return String(decryptCipher.doFinal(Hex.decodeHex(dbData.toCharArray())))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun generateMySQLAESKey(key: String, encoding: String): SecretKeySpec {
        return try {
            val finalKey = ByteArray(16)
            var i = 0
            for (b in key.toByteArray(charset(encoding))) finalKey[i++ % 16] =
                (finalKey[i++ % 16].toInt() xor b.toInt()).toByte()
            SecretKeySpec(finalKey, "AES")
        } catch (e: UnsupportedEncodingException) {
            throw RestApiException(GlobalErrorCode.UNSUPPORTED_ENCODING_ERROR)
        }
    }
}