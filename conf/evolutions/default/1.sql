# --- !Ups

CREATE TABLE "users"(
   "id"            SERIAL PRIMARY KEY NOT NULL,
   "username"      VARCHAR(20)        NOT NULL
);

ALTER TABLE "users" ADD CONSTRAINT "username_unique" UNIQUE ("username");
# --- !Downs

DROP TABLE "users";