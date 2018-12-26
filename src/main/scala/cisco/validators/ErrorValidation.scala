package cisco.validators

trait ErrorValidation {
  def errorMessage: String
}

object NoData extends ErrorValidation {
  def errorMessage: String = "Data is missing."
}

object ArtistGrouIdNotNumber extends ErrorValidation {
  def errorMessage: String = "Artist group id can be a number only."
}

object ArtistGroupNameIsEmpty extends ErrorValidation {
  def errorMessage: String = "Artist group name cannot be empty."
}

object ArtistGroupCategoryIsEmpty extends ErrorValidation {
  def errorMessage: String = "Artist group category cannot contain special characters or be empty."
}

object ArtistGroupOutstandingNotBoolean extends ErrorValidation {
  def errorMessage: String = "Artist group outstanding can be 'true' or 'false'."
}

object ArtistGroupPictureNotValid extends ErrorValidation {
  def errorMessage: String = "Artist group picture name file contains '.jpg'."
}


