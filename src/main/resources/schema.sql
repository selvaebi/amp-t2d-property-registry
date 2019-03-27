CREATE TABLE IF NOT EXISTS phenotype (
  id                 VARCHAR(255) NOT NULL,
  allowed_values     VARCHAR(255) NOT NULL,
  created_date       TIMESTAMP,
  description        TEXT         NOT NULL,
  last_modified_date TIMESTAMP,
  phenotype_group    VARCHAR(255) NOT NULL,
  type               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS property (
  id                 VARCHAR(255) NOT NULL,
  created_date       TIMESTAMP,
  description        TEXT         NOT NULL,
  last_modified_date TIMESTAMP,
  meaning            VARCHAR(255) NOT NULL,
  type               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS registry_user (
  email VARCHAR(255) NOT NULL,
  role  VARCHAR(255),
  PRIMARY KEY (email)
);