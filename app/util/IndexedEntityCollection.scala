package util

import models.BaseModelIdReader

case class IndexedEntityCollection[T, K](entities: Seq[T], entitiesById: Map[K, T])

object IndexedEntityCollection {

  def apply[T, K](entities: Seq[T])(implicit idReader: BaseModelIdReader[T, K]): IndexedEntityCollection[T, K] =
    IndexedEntityCollection[T, K](entities, entities.map { entity => idReader.getId(entity) -> entity }.toMap)

}