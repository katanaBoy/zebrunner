DROP TABLE IF EXISTS auth_provider;
CREATE TABLE auth_provider (
    id SERIAL,
    name VARCHAR(20) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
CREATE TRIGGER update_timestamp_auth_provider BEFORE INSERT OR UPDATE ON auth_provider FOR EACH ROW EXECUTE PROCEDURE update_timestamp();