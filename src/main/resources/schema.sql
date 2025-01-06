CREATE TABLE IF NOT EXISTS Book (
   id INT NOT NULL,
   title varchar(250) NOT NULL,
   description text NOT NULL,
   version int,
   PRIMARY KEY (id)
);