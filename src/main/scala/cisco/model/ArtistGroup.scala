package cisco.model

case class ArtistGroup(id: String,
                       name: String,
                       category: String,
                       outstanding: Boolean,
                       musicians: List[Artist],
                       picture: String,
                       description: String)

case class Artist (firstName: String, lastName: String)
