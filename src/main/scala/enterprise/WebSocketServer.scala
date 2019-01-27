package enterprise

import cats.effect._
import cats.implicits._
import doobie.util.transactor.Transactor
import enterprise.model.{Artist, ArtistGroup}
import enterprise.repositories.{ArtistGroupRepository, FlowRepository}
import fs2._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeServerBuilder

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext


object WebSocketServer extends IOApp {

  import scala.concurrent.ExecutionContext.Implicits.global

  val bands = List(
    ArtistGroup("1", "Pink Floyd", "Psychedelic", true,
      List(
        Artist("David","Gilmour"),
        Artist("Rick", "Wright"),
        Artist("Roger", "Waters"),
        Artist("Nick", "Mason"),
        Artist("Syd", "Barret")),
        "pink_floyd.jpg",
        """Pink Floyd were an English rock band formed in London in 1965. They achieved international acclaim with their progressive and psychedelic music. Distinguished by their philosophical lyrics, sonic experimentation, extended compositions, and elaborate live shows, they are one of the most commercially successful and influential groups in popular music history.Pink Floyd were founded by students Syd Barrett on guitar and lead vocals, Nick Mason on drums, Roger Waters on bass and vocals, and Richard Wright on keyboards and vocals. They gained popularity performing in London's underground music scene during the late 1960s, and under Barrett's leadership released two charting singles and a successful debut album, The Piper at the Gates of Dawn (1967). Guitarist and vocalist David Gilmour joined in December 1967; Barrett left in April 1968 due to deteriorating mental health. Waters became the band's primary lyricist and conceptual leader, devising the concepts behind their albums The Dark Side of the Moon (1973), Wish You Were Here (1975), Animals (1977), The Wall (1979) and The Final Cut (1983). The Dark Side of the Moon and The Wall became two of the best-selling albums of all time. Following creative tensions, Wright left Pink Floyd in 1979, followed by Waters in 1985. Gilmour and Mason continued as Pink Floyd; Wright rejoined them as a session musician and, later, band member. The three produced two more albums—A Momentary Lapse of Reason (1987) and The Division Bell (1994)—and toured through 1994. After nearly two decades of enmity, Gilmour, Wright, and Mason reunited with Waters in 2005 to perform as Pink Floyd in London as part of the global awareness event Live 8; Gilmour and Waters stated they had no further plans to reunite the band. Barrett died in 2006, and Wright in 2008. The last Pink Floyd studio album, The Endless River (2014), was recorded without Waters and based almost entirely on unreleased material. Pink Floyd were inducted into the American Rock and Roll Hall of Fame in 1996 and the UK Music Hall of Fame in 2005. By 2013, they had sold more than 250 million records worldwide"""),
    ArtistGroup("2", "The Beatles", "Rock", true,
      List(
        Artist("John", "Lennon"),
        Artist("Paul", " McCartney"),
        Artist("George", "Harrison"),
        Artist("Ringo", "Starr")
      ),
        "beatles.jpg",
        """The Beatles were an English rock band formed in Liverpool in 1960. With members John Lennon, Paul McCartney, George Harrison and Ringo Starr, they became widely regarded as the foremost and most influential music band in history.[1] In 1963, their enormous popularity first emerged as "Beatlemania"; as the group's music grew in sophistication, led by primary songwriters Lennon and McCartney, the band became integral to pop music's evolution into an art form and to the development of the counterculture of the 1960s.[2] Rooted in skiffle, beat and 1950s rock and roll, the Beatles later experimented with several musical styles, ranging from pop ballads and Indian music to psychedelia and hard rock, often incorporating classical elements and unconventional recording techniques in innovative ways. #The Beatles built their reputation playing clubs in Liverpool and Hamburg over a three-year period from 1960, with Stuart Sutcliffe initially serving as bass player. The core trio of Lennon, McCartney and Harrison, together since 1958, went through a succession of drummers, including Pete Best, before asking Starr to join them in 1962. Manager Brian Epstein moulded them into a professional act, and producer George Martin guided and developed their recordings, greatly expanding the group's popularity in the United Kingdom after their first hit, "Love Me Do", in late 1962. They acquired the nickname "the Fab Four" as Beatlemania grew in Britain over the next year, and by early 1964 became international stars, leading the "British Invasion" of the United States pop market. From 1965 onwards, the Beatles produced increasingly innovative recordings, including the albums Rubber Soul (1965), Revolver (1966), Sgt. Pepper's Lonely Hearts Club Band (1967), The Beatles (also known as the "White Album", 1968) and Abbey Road (1969). After their break-up in 1970, they each enjoyed success as solo artists. Lennon was shot and killed in December 1980, and Harrison died of lung cancer in November 2001. McCartney and Starr remain musically active. The Beatles are the best-selling band in history, with estimated sales of over 800 million records worldwide. They are the best-selling music artists in the United States, with 178 million certified units. The group was inducted into the Rock and Roll Hall of Fame in 1988, and all four main members were inducted individually from 1994 to 2015. They have also had more number-one albums on the British charts and sold more singles in the UK than any other act. In 2008, the group topped Billboard magazine's list of the all-time most successful artists; as of 2017, they hold the record for most number-one hits on the Hot 100 chart with twenty. They have received seven Grammy Awards, an Academy Award for Best Original Song Score and fifteen Ivor Novello Awards. They were also collectively included in Time magazine's compilation of the twentieth century's 100 most influential people.""".stripMargin('#')),
    ArtistGroup("3", "King Crimson", "Progressive Rock", true,
      List(
        Artist("Robert", "Fripp"),
        Artist("Jakko", " Jakszyk"),
        Artist("Tony", "Levin"),
        Artist("Mel", "Collins"),
        Artist("Pat", "Mastelotto"),
        Artist("Gavin", "Harrison"),
        Artist("Jeremy", "Stacey"),
        Artist("Bill", "Rieflin")
      )
    ,   "king_crimson.jpg",
        """King Crimson are an English progressive rock band formed in London in 1968. King Crimson have been influential both on the
          early 1970s progressive rock movement and numerous contemporary artists. The band has undergone numerous formations throughout
          its history of which 22 musicians have been members; since October 2017 it has consisted of Robert Fripp, Jakko Jakszyk,
          Tony Levin, Mel Collins, Pat Mastelotto, Gavin Harrison, Jeremy Stacey and Bill Rieflin. Fripp is the only consistent
          member of the group and is considered the band's leader and driving force. The band has earned a large cult following.
          They were ranked No. 87 on VH1's 100 Greatest Artists of Hard Rock.[6] Although considered to be a seminal progressive
          rock band (a genre characterised by extended instrumental sections and complex song structures), they have often distanced
          themselves from the genre: as well as influencing several generations of progressive and psychedelic rock bands, they have also been
          an influence on subsequent alternative metal, hardcore and experimental/noise musicians.
          #
          #Developed from the unsuccessful psychedelic pop trio Giles, Giles and Fripp, the initial King Crimson were key to the formation of
          early progressive rock, strongly influencing and altering the music of contemporaries such as Yes and Genesis.[7] Their debut album,
          In the Court of the Crimson King (1969), remains their most successful and influential release, with its elements of jazz, classical
          and experimental music.[8] Their success increased following an opening act performance for the Rolling Stones at Hyde Park, London,
          in 1969. Following In the Wake of Poseidon (1970) and the less successful chamber jazz-inspired Lizard (1970), and Islands (1971),
          the group reformatted and changed their instrumentation (swapping out saxophone in favour of violin and unusual percussion) in order
          to develop their own take on European rock improvisation, reaching a new creative peak on Larks' Tongues in Aspic (1973),
          Starless and Bible Black (1974) and Red (1974). Fripp disbanded the group in 1974.
          #
          #In 1981, King Crimson reformed with another change in musical direction and instrumentation (incorporating, for the first time,
          a mixture of British and American personnel plus doubled guitar and influences taken from gamelan, post-punk and New York
          minimalism). This lasted for three years, resulting in the trio of albums Discipline (1981), Beat (1982) and Three of a Perfect
          Pair (1984). Following a decade-long hiatus, Fripp revived the group as an expanded "Double Trio" sextet in 1994, mingling its
          mid-‘70s and 1980s approaches with new creative options available via MIDI technology. This resulted in another three-year cycle
          of activity including the release of Thrak (1995). King Crimson reunited again in 2000 as a more alternative metal-oriented
          quartet (or "Double Duo"), releasing The Construkction of Light in 2000 and The Power to Believe in 2003: after further personnel
          shuffles, the band expanded to a double-drummer quintet for a 2008 tour celebrating their 40th anniversary.
          #
          #Following another hiatus between 2009 and 2012, King Crimson reformed once again in 2013; this time as a septet (and, later,
          octet) with an unusual three-drumkit frontline and the return of saxophone/flute to the lineup for the first time since 1971.
          This current version of King Crimson has continued to tour and to release live albums, significantly rearranging and
          reinterpreting music from across the band’s entire previous career.
          #
          #Since 1997, several musicians have pursued aspects of the band's work and approaches through a series of related bands
          collectively referred to as ProjeKcts.""".stripMargin('#')),
    ArtistGroup("4", "Led Zeppelin", "Rock", true,
      List(
        Artist("Robert", "Plant"),
        Artist("Jimmy", " Page"),
        Artist("John", "Bonham"),
        Artist("John Paul", "Jones")
      ),
        "led_zeppelin.jpg",
        """Led Zeppelin were an English rock band formed in London in 1968. The group consisted of guitarist Jimmy Page, singer Robert Plant,
            bassist and keyboardist John Paul Jones, and drummer John Bonham. The band's heavy, guitar-driven sound has led them to be cited
            as one of the progenitors of heavy metal. Their style drew from a wide variety of influences, including blues, psychedelia,
            and folk music.
            #
            #After changing their name from the New Yardbirds, Led Zeppelin signed a deal with Atlantic Records that afforded them
            considerable artistic freedom. Although the group were initially unpopular with critics, they achieved significant commercial
            success with eight studio albums released over eleven years, from Led Zeppelin (1969) to In Through the Out Door (1979).
            Their untitled fourth studio album, commonly known as Led Zeppelin IV (1971) and featuring the song "Stairway to Heaven",
            is among the most popular and influential works in rock music, and it helped to secure the group's popularity.
            #
            #Page wrote most of Led Zeppelin's music, particularly early in their career, while Plant generally supplied the lyrics. Jones'
            keyboard-based compositions later became central to the group's catalogue, which featured increasing experimentation.
            The latter half of their career saw a series of record-breaking tours that earned the group a reputation for excess
            and debauchery. Although they remained commercially and critically successful, their output and touring schedule were
            limited during the late 1970s, and the group disbanded following Bonham's death from alcohol-related asphyxia in 1980.
            In the decades that followed, the surviving members sporadically collaborated and participated in one-off Led Zeppelin
            reunions. The most successful of these was the 2007 Ahmet Ertegun Tribute Concert in London, with Jason Bonham taking his
            late father's place behind the drums.

            #
            #Many critics consider Led Zeppelin to be one of the most successful, innovative, and influential rock groups in history.
            They are one of the best-selling music artists in the history of audio recording; various sources estimate the group's
            record sales at 200 to 300 million units worldwide. With RIAA-certified sales of 111.5 million units, they are the
            third-best-selling band in the US. Each of their nine studio albums placed in the top 10 of the Billboard album chart
            and six reached the number-one spot. They achieved eight consecutive UK number-one albums. Rolling Stone magazine described
            them as "the heaviest band of all time", "the biggest band of the Seventies", and "unquestionably one of the most enduring
            bands in rock history". They were inducted into the Rock and Roll Hall of Fame in 1995; the museum's biography of the
            band states that they were "as influential" during the 1970s as the Beatles were during the 1960s.""".stripMargin('#')),
    ArtistGroup("5", "Rolling Stones", "Rock", true,
      List(
        Artist("Mick", "Jagger"),
        Artist("Keith", "Richards"),
        Artist("Ronnie", "Wood"),
        Artist("Brian", "Jones")
      ),
        "rolling_stones.jpg",
    """The Rolling Stones are an English rock band formed in London in 1962. The first stable line-up consisted of Brian Jones (guitar,
        harmonica), Mick Jagger (lead vocals), Keith Richards (guitar, backing vocals), Bill Wyman (bass), Charlie Watts (drums), and
        Ian Stewart (piano). Stewart was removed from the official line-up in 1963 but continued as a touring member until his death
        in 1985. Brian Jones was the original leader of the group. The band's primary songwriters, Jagger and Richards, assumed leadership
        after Andrew Loog Oldham became the group's manager. Their musical focus shifted from covering blues songs to writing original
        material, a decision with which Jones did not agree. Jones left the band less than a month before his death in 1969,
        having already been replaced by Mick Taylor, who remained until 1974. After Taylor left the band, Ronnie Wood took his place
        in 1975 and continues on guitar in tandem with Richards. Following Wyman's departure in 1993, Darryl Jones joined as their
        touring bassist. The Stones' touring keyboardists have included Nicky Hopkins (1967–1982), Ian McLagan (1978–1981),
        Billy Preston (through the mid-1970s) and Chuck Leavell (1982–present).
        #
        #The Rolling Stones were at the forefront of the British Invasion of bands that became popular in the United States in 1964
        and were identified with the youthful and rebellious counterculture of the 1960s. Rooted in blues and early rock and roll,
        the band started out playing covers but found more success with their own material; songs such as "(I Can't Get No) Satisfaction"
        and "Paint It Black" became international hits. After a short period of experimentation with psychedelic rock in the mid-1960s,
        the group returned to its "bluesy" roots with Beggars Banquet (1968), which along with its follow-ups Let It Bleed (1969),
        Sticky Fingers (1971) and Exile on Main St. (1972), is generally considered to be the band's best work and is seen as their
        "Golden Age". It was during this period they were first introduced on stage as "The Greatest Rock and Roll Band in the World".
        #
        #The band continued to release commercially successful albums through the 1970s and early 1980s, including Some Girls (1978) and
        Tattoo You (1981), the two best-sellers in their discography. From 1983 to 1987 tensions between Jagger and Richards almost
        caused the band to split; however, they overcame their differences and rekindled their friendship after a temporary separation
        to work on solo projects. The Stones experienced a comeback with Steel Wheels (1989), promoted by a large stadium and arena tour.
        Since the 1990s, the group has lost much of their mainstream relevancy and new material has been less frequent. Despite this,
        the Rolling Stones continue to be a huge attraction on the live circuit. By 2007, the band had four of the top five highest-grossing
        concert tours of all time: Voodoo Lounge Tour (1994–1995), Bridges to Babylon Tour (1997–1998), Licks Tour (2002–2003)
        and A Bigger Bang Tour (2005–2007).[3] Musicologist Robert Palmer attributes the endurance of the Rolling Stones to their
        being "rooted in traditional verities, in rhythm-and-blues and soul music", while "more ephemeral pop fashions have come
        and gone".
        #
        #The Rolling Stones were inducted into the Rock and Roll Hall of Fame in 1989 and the UK Music Hall of Fame in 2004.
        Rolling Stone magazine ranked them fourth on the "100 Greatest Artists of All Time" list and their estimated record sales
        are above 250 million. They have released 30 studio albums, 23 live albums and numerous compilations. Let It Bleed (1969)
        marked the first of five consecutive No. 1 studio and live albums in the UK. Sticky Fingers (1971) was the first of eight
        consecutive No. 1 studio albums in the US. In 2008, the band ranked 10th on the Billboard Hot 100 All-Time Top Artists chart.
        In 2012, the band celebrated its 50th anniversary.""".stripMargin('#')),
    ArtistGroup("6", "Genesis", " Progressive Rock", true,
      List(
        Artist("Phil", "Collins"),
        Artist("Steve", "Hackett"),
        Artist("Mike", "Rutherford"),
        Artist("Tony", "Banks")
      ),
      "genesis.jpg",
      """Genesis were an English rock band formed at Charterhouse School, Godalming, Surrey in 1967. The most successful and longest-lasting
        line-up consisted of keyboardist Tony Banks, bassist/guitarist Mike Rutherford and drummer/singer Phil Collins. Significant former
        members were guitarist Steve Hackett and original lead singer Peter Gabriel. The band moved from folk music to progressive rock
        in the 1970s, before moving towards pop at the end of the decade. They have sold 21.5 million albums in the United States,
        with worldwide sales of between 100 million and 150 million.
        #
        #Formed by five Charterhouse pupils including Banks, Rutherford, Gabriel, and Anthony Phillips, Genesis were named by former
        pupil Jonathan King, who arranged for them to record several unsuccessful singles and their debut album From Genesis to
        Revelation in 1968. After splitting with King, the group began to tour professionally, signed with Charisma Records and recorded
        Trespass (1970) in the progressive rock style. Following the departure of Phillips, Genesis recruited Collins and Hackett and
        recorded Nursery Cryme (1971). Their live shows also began to be centred on Gabriel's theatrical costumes and performances.
        They were first successful in mainland Europe, before entering the UK charts with Foxtrot (1972). In 1973, they released
        Selling England by the Pound (1973), which featured their first UK top 30 single "I Know What I Like (In Your Wardrobe)".
        The concept album The Lamb Lies Down on Broadway followed in 1974, and was promoted with a transatlantic tour featuring an
        elaborate stage show. Following the Lamb tour, Gabriel left Genesis in August 1975 to begin a solo career.
        #
        #After an unsuccessful search for a replacement, Collins took over as lead singer, while Genesis gained popularity in the UK
        and the US. Following A Trick of the Tail and Wind & Wuthering (both 1976), Hackett left, reducing the band to Banks, Rutherford,
        and Collins. Genesis' next album ...And Then There Were Three... produced their first UK top ten and US top 30 single in 1978
        with "Follow You Follow Me", and they continued to gain success with Duke (1980), Abacab (1981), and Genesis (1983),
        reaching a peak with Invisible Touch (1986), which featured five US top five singles. Its title track reached number one
        in the US. After the tour for We Can't Dance (1991), Collins left Genesis in 1996 to focus on his solo career. Banks and
        Rutherford recruited Ray Wilson for Calling All Stations (1997), but a lack of success in the US led to a group hiatus. Banks,
        Rutherford and Collins reunited for the Turn It On Again Tour in 2007, and with Gabriel and Hackett were interviewed for the
        2014 BBC documentary Genesis: Together and Apart.
        #
        #Their discography includes fifteen studio and six live albums, six of which topped the UK chart. They have won numerous awards
        and nominations, including a Grammy Award for Best Concept Music Video with "Land of Confusion", and inspired a number of tribute
        bands recreating Genesis shows from various stages of the band's career. In 2010, Genesis were inducted into the Rock and
        Roll Hall of Fame.""".stripMargin('#'))
  )

  override def run(args: List[String]): IO[ExitCode] = {
    val xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", "jdbc:postgresql:mixed", "esabirzh", ""
    )
    new Initializer[IO].config(bands, xa).compile.drain.as(ExitCode.Success)
  }


}

class Initializer[F[_]](implicit F: ConcurrentEffect[F], timer: Timer[F]) extends Http4sDsl[F] {


  def config(groups: List[ArtistGroup], xa: Transactor.Aux[F, Unit])(implicit ec: ExecutionContext) = {
    val ipAddress = "localhost"
  //  val ipAddress = "10.83.179.14"

    Stream.eval(ArtistGroupRepository.initialise[F](groups.to[ListBuffer])).flatMap { gRep =>
      Stream.eval(FlowRepository.initialise[F](xa)).flatMap { fRep =>
        BlazeServerBuilder[F]
          .bindHttp(8080, ipAddress)
          .withWebSockets(true)
          .withHttpApp(new WebSocketRoutes[F](gRep, fRep).routes.orNotFound)
          .serve
      }
    }
  }

}

