# --- !Ups

CREATE TABLE "comments"(
   "id"               SERIAL PRIMARY KEY NOT NULL,
   "user"             INT                NOT NULL,
   "repository_owner" VARCHAR(100)       NOT NULL,
   "repository_name"  VARCHAR(100)       NOT NULL,
   "comment"          VARCHAR(500),
   "created_on"       TIMESTAMP          NOT NULL  DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT user_fk FOREIGN KEY("user")    REFERENCES "users"("id") ON DELETE RESTRICT,
);

# --- !Downs

DROP TABLE "comments";