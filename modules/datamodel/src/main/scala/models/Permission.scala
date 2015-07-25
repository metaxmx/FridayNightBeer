package models

case class Permission(
    name: String,
    restriction: AccessRestriction)