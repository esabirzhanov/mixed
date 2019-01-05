package enterprise.validators

import cats.data.ValidatedNel
import cats.implicits._
import enterprise.model.{Artist, ArtistGroup}
import io.circe.{Encoder, Json}

trait ArtistGroupFormValidator {

  type ValidationResult[A] = ValidatedNel[ErrorValidation, A]

  def validateId(id: Option[String]): ValidationResult[String] =
    id match {
      case Some(v) => if (v.matches("^[0-9]+$")) v.validNel else ArtistGrouIdNotNumber.invalidNel
      case None => NoData.invalidNel
    }

  def validateName(name: Option[String]): ValidationResult[String] =
    name match {
      case Some(v) => if (v.matches("[A-Za-z0-9\\s]+")) v.validNel else ArtistGroupNameIsEmpty.invalidNel
      case None => NoData.invalidNel
    }

  def validateCategory(category: Option[String]): ValidationResult[String] =
    category match {
      case Some(v) =>  if (v.matches("^[A-Za-z0-9\\s]+")) v.validNel else ArtistGroupCategoryIsEmpty.invalidNel
      case None => NoData.invalidNel
    }

  def validateOutstanding(outstanding: Option[String]): ValidationResult[Boolean] =
    outstanding match {
      case Some(_) => true.validNel
      case None => NoData.invalidNel

    }

  def validateMusicians(musicians: Option[List[Artist]]): ValidationResult[List[Artist]] =
    musicians match {
      case Some(v) => v.validNel
      case None => NoData.invalidNel
    }

  def validatePicture(picture: Option[String]): ValidationResult[String] =
    picture match {
      case Some(v) =>  if (v.matches("$.jpg")) v.validNel else ArtistGroupPictureNotValid.invalidNel
      case None => NoData.invalidNel
    }


  def validateDescription(description: Option[String]): ValidationResult[String] =
    description match {
      case Some(v) =>  v.validNel
      case None => NoData.invalidNel
    }


  def validateForm(id: Option[String], name: Option[String], category: Option[String],
                   outstanding: Option[String], picture: Option[String], description: Option[String]): ValidationResult[ArtistGroup] = {
    (validateId(id),
      validateName(name),
      validateCategory(category),
      validateOutstanding(outstanding),
      validateMusicians(Some(List())),
      validatePicture(picture),
      validateDescription(description),
    ).mapN(ArtistGroup)
  }
}

object ArtistGroupFormValidator extends ArtistGroupFormValidator {
  implicit val encodeErrorValidation: Encoder[ErrorValidation] = { error =>
    Json.obj(("error", Json.fromString(error.errorMessage)))
  }
}
