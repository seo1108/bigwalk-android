package kr.co.bigwalk.app.data.organization

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.Serializable

data class Department (
    override val id: Long,
    override var name: String,
    @TypeConverters override val organizationIdType: OrganizationIdType? = null,
    override val personInChargePhoneNumber: String? = null,
    override val personInChargeName: String? = null,
    override val organizationId: Long? = 0,
    override val enabled: Boolean? = false,
    override val depth: Long? = 1
) : OrganizationItem,Serializable


object OrganizationTypeConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(type: String?): OrganizationIdType? {
        return type?.let { OrganizationIdType.valueOf(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toString(type: OrganizationIdType?): String? {
        return type?.name
    }
}