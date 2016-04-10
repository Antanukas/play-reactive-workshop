package repositories

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

class JdbcProfileProvider @Inject()(val dbConfigProvider: DatabaseConfigProvider) {

  implicit val provider = dbConfigProvider.get[JdbcProfile]
}
