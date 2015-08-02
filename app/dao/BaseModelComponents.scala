package dao

import models.BaseModelSpec

trait BaseModelComponents[T, K] {

  def spec: BaseModelSpec[T, K]

  implicit val baseModel = spec.baseModel

  implicit val baseModelIdReader = spec.baseModelIdReader

  implicit val baseModelIdWriter = spec.baseModelIdWriter

  implicit val bsonFormat = spec.bsonHandler

}