package kr.co.bigwalk.app.data.organization.repository

import androidx.annotation.NonNull
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.organization.dto.CertNoRequest
import kr.co.bigwalk.app.data.organization.dto.OrganizationRequest
import kr.co.bigwalk.app.data.organization.space.SpaceOrganizationResponse
import kr.co.bigwalk.app.data.organization.space.SpaceUserRequest
import kr.co.bigwalk.app.util.DebugLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OrganizationRemoteDataSource : OrganizationDataSource {

    override fun getOrganizations(getOrganizationsCallback: OrganizationDataSource.GetOrganizationsCallback) {
        RemoteApiManager.getService().getOrganizations().enqueue(object : Callback<List<Organization>> {
            override fun onFailure(call: Call<List<Organization>>, t: Throwable) {
                getOrganizationsCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Organization>>, response: Response<List<Organization>>) {
                DebugLog.d(call.request().toString())
                DebugLog.d(call.request().body.toString())
                when (response.code()) {
                    200 -> getOrganizationsCallback.onSuccess(response.body())
                    else -> getOrganizationsCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun getOrganization(organizationId: Long, getOrganizationCallback: OrganizationDataSource.GetOrganizationCallback) {
        RemoteApiManager.getService().getOrganization(organizationId).enqueue(object : Callback<Organization> {
            override fun onFailure(call: Call<Organization>, t: Throwable) {
                getOrganizationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                DebugLog.d(call.request().toString())
                DebugLog.d(call.request().body.toString())
                when (response.code()) {
                    200 -> getOrganizationCallback.onSuccess(response.body())
                    else -> getOrganizationCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun getOrganizationRequirement(organizationId: Long, getOrganizationRequirementCallback: OrganizationDataSource.GetOrganizationRequirementCallback) {
        RemoteApiManager.getService().getOrganizationRequirement(organizationId).enqueue(object : Callback<OrganizationRequirement> {
            override fun onFailure(call: Call<OrganizationRequirement>, t: Throwable) {
                getOrganizationRequirementCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<OrganizationRequirement>, response: Response<OrganizationRequirement>) {
                DebugLog.d(call.request().toString())
                DebugLog.d(call.request().body.toString())
                when (response.code()) {
                    200 -> getOrganizationRequirementCallback.onSuccess(response.body())
                    else -> getOrganizationRequirementCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun getDepartments(organizationId: Long, getDepartmentsCallback: OrganizationDataSource.GetDepartmentsCallback) {
        RemoteApiManager.getService().getDepartments(organizationId).enqueue(object : Callback<List<Department>> {
            override fun onFailure(call: Call<List<Department>>, t: Throwable) {
                getDepartmentsCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Department>>, response: Response<List<Department>>) {
                when (response.code()) {
                    200 -> getDepartmentsCallback.onSuccess(response.body())
                    else -> getDepartmentsCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun getSubDepartments(organizationId: Long, depth: Long, parentId: Long, getSubDepartmentsCallback: OrganizationDataSource.GetSubDepartmentsCallback) {
        RemoteApiManager.getService().getSubDepartments(organizationId, depth, parentId).enqueue(object : Callback<List<Department>> {
            override fun onFailure(call: Call<List<Department>>, t: Throwable) {
                getSubDepartmentsCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Department>>, response: Response<List<Department>>) {
                when (response.code()) {
                    200 -> getSubDepartmentsCallback.onSuccess(response.body())
                    else -> getSubDepartmentsCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun getSearchKeyword(organizationId: Long, getSearchKeywordCallback: OrganizationDataSource.GetSearchKeywordCallback) {
        RemoteApiManager.getService().getSearchKeyword(organizationId).enqueue(object : Callback<List<Content>> {
            override fun onFailure(call: Call<List<Content>>, t: Throwable) {
                getSearchKeywordCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Content>>, response: Response<List<Content>>) {
                when (response.code()) {
                    200 -> getSearchKeywordCallback.onSuccess(response.body())
                    else -> getSearchKeywordCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun verifyAuthNum(organizationId: Long, request: CertNoRequest, verifyAuthNumCallback: OrganizationDataSource.VerifyAuthNumCallback) {
        RemoteApiManager.getService().verifyAuthNum(organizationId, request).enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                verifyAuthNumCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                when (response.code()) {
                    200 -> verifyAuthNumCallback.onSuccess(response.body()!!)
                    else -> verifyAuthNumCallback.onFailed(response.code().toString())
                }
            }
        })
    }

    override fun saveOrganizationInfo(request: OrganizationRequest, saveOrganizationInfoCallback: OrganizationDataSource.SaveOrganizationInfoCallback) {
        RemoteApiManager.getService().saveOrganizationInfo(request).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                saveOrganizationInfoCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> saveOrganizationInfoCallback.onSuccess()
                    else -> saveOrganizationInfoCallback.onFailed(response.code().toString())
                }
            }
        })
    }


    override fun getSpaceOrganization(organizationId: Long, getOrganizationCallback: OrganizationDataSource.GetSpaceOrganizationCallback) {
        RemoteApiManager.getService().getSpaceOrganization(organizationId).enqueue(object : Callback<SpaceOrganizationResponse> {
            override fun onFailure(call: Call<SpaceOrganizationResponse>, t: Throwable) {
                getOrganizationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<SpaceOrganizationResponse>, response: Response<SpaceOrganizationResponse>) {
                when (response.code()) {
                    200 -> getOrganizationCallback.onSuccess(response.body())
                    else -> getOrganizationCallback.onFailed(response.code().toString())
                }
            }


        })
    }

    override fun addProfileByOrganization(
        spaceUserRequest: SpaceUserRequest,
        addProfileByOrganizationCallback: OrganizationDataSource.AddProfileByOrganizationCallback
    ) {
        val map = createBodyFromSpaceUserRequest(spaceUserRequest)
        RemoteApiManager.getService().addProfileByOrganization(map).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                addProfileByOrganizationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    201 -> addProfileByOrganizationCallback.onSuccess()
                    else -> addProfileByOrganizationCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun modifyProfileByOrganization(
        spaceUserRequest: SpaceUserRequest,
        modifyProfileByOrganizationCallback: OrganizationDataSource.ModifyProfileByOrganizationCallback
    ) {
        val map = createBodyFromSpaceUserRequest(spaceUserRequest)
        RemoteApiManager.getService().modifyProfileByOrganization(map).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                modifyProfileByOrganizationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> modifyProfileByOrganizationCallback.onSuccess()
                    else -> modifyProfileByOrganizationCallback.onFailed(response.code().toString())
                }
            }
        })
    }

    @NonNull
    private fun createBodyFromSpaceUserRequest(request: SpaceUserRequest): HashMap<String, RequestBody> {
        val map: HashMap<String, RequestBody> = HashMap()
        map["organizationId"] = request.organizationId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        map["departmentId"] = request.departmentId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        if (request.value1.isNotEmpty()) {
            map["value1"] = request.value1.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        if (request.value2.isNotEmpty()) {
            map["value2"] = request.value2.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        if (request.value3.isNotEmpty()) {
            map["value3"] = request.value3.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        if (request.value4.isNotEmpty()) {
            map["value4"] = request.value4.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        if (request.value5.isNotEmpty()) {
            map["value5"] = request.value5.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        return map
    }

}