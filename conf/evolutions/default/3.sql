# --- !Ups

CREATE TABLE "comment_likes"(
   "id"               SERIAL PRIMARY KEY NOT NULL,
   "comment"          INT                NOT NULL,
   "user"             INT                NOT NULL,

   CONSTRAINT cm_likes_user_fk FOREIGN KEY("user") REFERENCES "users"("id") ON DELETE RESTRICT,
   CONSTRAINT cm_likes_cm_fk FOREIGN KEY("comment") REFERENCES "comments"("id") ON DELETE RESTRICT,
);

# --- !Downs

DROP TABLE "comment_likes";