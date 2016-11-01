package viewmodels

import viewmodels.GeneralViewModels.{OptPermissionMap, PermissionMap}

/**
  * View models for Forum/Category administration.
  */
object ForumAdminViewModels {

  /*
   * List Categories and Forums
   */

  case class ListCategoriesForum(id: String, name: String, url: Option[String], description: Option[String],
                                 readonly: Boolean, forumPermissions: PermissionMap,
                                 threadPermissions: PermissionMap, empty: Boolean)

  case class ListCategoriesCategory(id: String, name: String, forumPermissions: PermissionMap,
                                    threadPermissions: PermissionMap, forums: Seq[ListCategoriesForum])

  case class ListCategoriesResult(success: Boolean, categories: Seq[ListCategoriesCategory]) extends ViewModel

  /*
   * Create Category
   */

  case class CreateForumCategoryRequest(name: String, forumPermissions: OptPermissionMap,
                                        threadPermissions: OptPermissionMap) extends ViewModel


  case class CreateForumCategoryResult(success: Boolean,
                                       id: String) extends ViewModel

  /*
   * Edit Category
   */

  /*
   * Create Forum
   */

  case class CreateForumRequest(name: String,
                                url: Option[String],
                                description: Option[String],
                                readonly: Option[Boolean],
                                forumPermissions: OptPermissionMap,
                                threadPermissions: OptPermissionMap)

  case class CreateForumResult(success: Boolean,
                               id: String) extends ViewModel

}
