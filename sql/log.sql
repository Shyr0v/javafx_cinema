DROP TRIGGER IF EXISTS franchise_create ON franchise;
DROP TRIGGER IF EXISTS franchise_update ON franchise;
DROP TRIGGER IF EXISTS franchise_delete ON franchise;

DROP TRIGGER IF EXISTS cinema_create ON cinema;
DROP TRIGGER IF EXISTS cinema_update ON cinema;
DROP TRIGGER IF EXISTS cinema_delete ON cinema;

DROP FUNCTION IF EXISTS trigger_franchise_create();
DROP FUNCTION IF EXISTS trigger_franchise_update();
DROP FUNCTION IF EXISTS trigger_franchise_delete();

DROP FUNCTION IF EXISTS trigger_cinema_create();
DROP FUNCTION IF EXISTS trigger_cinema_update();
DROP FUNCTION IF EXISTS trigger_cinema_delete();

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