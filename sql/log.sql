CREATE TABLE IF NOT EXISTS log (
                                   id_log SERIAL PRIMARY KEY,
                                   table_name VARCHAR(50) NOT NULL,
                                   operation VARCHAR(50) NOT NULL,
                                   date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   ancien_contenu TEXT,
                                   nouveau_contenu TEXT,
                                   id_utilisateur INTEGER,
                                   CONSTRAINT fk_log_utilisateur
                                       FOREIGN KEY (id_utilisateur)
                                           REFERENCES utilisateur(id_utilisateur)
                                           ON DELETE SET NULL
);