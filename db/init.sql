CREATE SCHEMA IF NOT EXISTS crediya_auth AUTHORIZATION postgres;

ALTER ROLE postgres IN DATABASE postgres SET search_path = crediya_auth, public;

CREATE TABLE IF NOT EXISTS crediya_auth.usuario (
  id BIGSERIAL PRIMARY KEY,
  nombres VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100),
  documento_identidad VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(150),
  id_rol BIGINT,
  activo BOOLEAN DEFAULT TRUE
);