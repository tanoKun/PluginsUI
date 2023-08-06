package com.github.tanokun.pluginsui.util

import com.github.tanokun.pluginsui.ui.ExtensionConfig
import java.io.File
import java.util.*

class AbstractFile(val file: File, uuid: UUID) {
    val canCreate: Boolean

    val canEdit: Boolean

    val canMove: Boolean

    val canDelete: Boolean

    val canDownload: Boolean

    val permissions = ExtensionConfig.accessPermissions[uuid] ?: arrayListOf()

    init {
        var canCreate = false
        var canEdit = false
        var canMove = false
        var canDelete = false
        var canDownload = false

        var parent: File? = file
        while (parent != null) {
            val permission = permissions.firstOrNull { it.path == parent!!.path }
            if (permission == null) {
                parent = parent.parentFile
                continue
            }

            canCreate = if (permission.canCreate) true else canEdit
            canEdit = if (permission.canEdit) true else canEdit
            canMove = if (permission.canMove) true else canMove
            canDelete = if (permission.canDelete) true else canDelete
            canDownload = if (permission.canDownload) true else canDownload

            parent = parent.parentFile ?: break
        }

        this.canCreate = canCreate
        this.canEdit = canEdit
        this.canMove = canMove
        this.canDelete = canDelete
        this.canDownload = canDownload
    }

    fun canShow(file: File): Boolean {
        var parent: File? = file
        while (parent != null) {
            val permission = permissions.firstOrNull { it.path == parent!!.path }
            if (permission != null) return true


            parent = parent.parentFile
            continue
        }

        return permissions.any { it.path.startsWith(file.path) }

    }
}