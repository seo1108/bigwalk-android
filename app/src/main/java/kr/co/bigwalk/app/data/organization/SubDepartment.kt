package kr.co.bigwalk.app.data.organization

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.Serializable

data class SubDepartment(
    override val id: Long?,
    override var name: String?,
    override val organizationId: Long?,
    override val enabled: Boolean?,
    override val depth: Long?,

) : SubDepartmentItem, Serializable
