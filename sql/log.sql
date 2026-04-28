-- =====================================================
-- TABLE DE LOG
-- =====================================================

DROP TABLE IF EXISTS log CASCADE;

CREATE TABLE log (
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

-- =====================================================
-- UTILISATEUR CONNECTE POUR LES LOGS
-- =====================================================

CREATE OR REPLACE FUNCTION set_current_user_id(user_id INTEGER)
RETURNS VOID AS $$
BEGIN
    PERFORM set_config('app.current_user_id', user_id::text, false);
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- FONCTION GENERIQUE D'INSERTION DANS LES LOGS
-- =====================================================

CREATE OR REPLACE FUNCTION insert_log_function(
    p_table_name VARCHAR,
    p_operation VARCHAR,
    p_ancien_contenu TEXT,
    p_nouveau_contenu TEXT
)
RETURNS VOID AS $$
DECLARE
v_user_id INTEGER;
BEGIN
BEGIN
        v_user_id := current_setting('app.current_user_id', true)::INTEGER;
EXCEPTION
        WHEN OTHERS THEN
            v_user_id := NULL;
END;

INSERT INTO log(
    table_name,
    operation,
    date_action,
    ancien_contenu,
    nouveau_contenu,
    id_utilisateur
)
VALUES(
          p_table_name,
          p_operation,
          NOW(),
          p_ancien_contenu,
          p_nouveau_contenu,
          v_user_id
      );
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- SUPPRESSION ANCIENS TRIGGERS
-- =====================================================

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

-- =====================================================
-- LOG FRANCHISE : INSERT
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_franchise_create()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'franchise',
        'INSERT',
        '',
        'ID: ' || NEW.id_franchise ||
        ', Nom: ' || NEW.nom_franchise ||
        ', Siège: ' || COALESCE(NEW.siege_social, '') ||
        ', ID gérant: ' || COALESCE(NEW.id_gerant::TEXT, 'aucun')
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER franchise_create
    AFTER INSERT ON franchise
    FOR EACH ROW EXECUTE FUNCTION trigger_franchise_create();

-- =====================================================
-- LOG FRANCHISE : UPDATE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_franchise_update()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'franchise',
        'UPDATE',
        'ID: ' || OLD.id_franchise ||
        ', Nom: ' || OLD.nom_franchise ||
        ', Siège: ' || COALESCE(OLD.siege_social, '') ||
        ', ID gérant: ' || COALESCE(OLD.id_gerant::TEXT, 'aucun'),

        'ID: ' || NEW.id_franchise ||
        ', Nom: ' || NEW.nom_franchise ||
        ', Siège: ' || COALESCE(NEW.siege_social, '') ||
        ', ID gérant: ' || COALESCE(NEW.id_gerant::TEXT, 'aucun')
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER franchise_update
    AFTER UPDATE ON franchise
    FOR EACH ROW EXECUTE FUNCTION trigger_franchise_update();

-- =====================================================
-- LOG FRANCHISE : DELETE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_franchise_delete()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'franchise',
        'DELETE',
        'ID: ' || OLD.id_franchise ||
        ', Nom: ' || OLD.nom_franchise ||
        ', Siège: ' || COALESCE(OLD.siege_social, '') ||
        ', ID gérant: ' || COALESCE(OLD.id_gerant::TEXT, 'aucun'),
        ''
    );
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER franchise_delete
    AFTER DELETE ON franchise
    FOR EACH ROW EXECUTE FUNCTION trigger_franchise_delete();

-- =====================================================
-- LOG CINEMA : INSERT
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_cinema_create()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'cinema',
        'INSERT',
        '',
        'ID: ' || NEW.id_cinema ||
        ', Dénomination: ' || NEW.denomination ||
        ', Adresse: ' || COALESCE(NEW.adresse, '') ||
        ', Ville: ' || COALESCE(NEW.ville, '') ||
        ', ID franchise: ' || NEW.id_franchise
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cinema_create
    AFTER INSERT ON cinema
    FOR EACH ROW EXECUTE FUNCTION trigger_cinema_create();

-- =====================================================
-- LOG CINEMA : UPDATE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_cinema_update()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'cinema',
        'UPDATE',
        'ID: ' || OLD.id_cinema ||
        ', Dénomination: ' || OLD.denomination ||
        ', Adresse: ' || COALESCE(OLD.adresse, '') ||
        ', Ville: ' || COALESCE(OLD.ville, '') ||
        ', ID franchise: ' || OLD.id_franchise,

        'ID: ' || NEW.id_cinema ||
        ', Dénomination: ' || NEW.denomination ||
        ', Adresse: ' || COALESCE(NEW.adresse, '') ||
        ', Ville: ' || COALESCE(NEW.ville, '') ||
        ', ID franchise: ' || NEW.id_franchise
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cinema_update
    AFTER UPDATE ON cinema
    FOR EACH ROW EXECUTE FUNCTION trigger_cinema_update();

-- =====================================================
-- LOG CINEMA : DELETE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_cinema_delete()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'cinema',
        'DELETE',
        'ID: ' || OLD.id_cinema ||
        ', Dénomination: ' || OLD.denomination ||
        ', Adresse: ' || COALESCE(OLD.adresse, '') ||
        ', Ville: ' || COALESCE(OLD.ville, '') ||
        ', ID franchise: ' || OLD.id_franchise,
        ''
    );
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cinema_delete
    AFTER DELETE ON cinema
    FOR EACH ROW EXECUTE FUNCTION trigger_cinema_delete();-- =====================================================
-- TABLE DE LOG
-- =====================================================

DROP TABLE IF EXISTS log CASCADE;

CREATE TABLE log (
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

-- =====================================================
-- UTILISATEUR CONNECTE POUR LES LOGS
-- =====================================================

CREATE OR REPLACE FUNCTION set_current_user_id(user_id INTEGER)
RETURNS VOID AS $$
BEGIN
    PERFORM set_config('app.current_user_id', user_id::text, false);
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- FONCTION GENERIQUE D'INSERTION DANS LES LOGS
-- =====================================================

CREATE OR REPLACE FUNCTION insert_log_function(
    p_table_name VARCHAR,
    p_operation VARCHAR,
    p_ancien_contenu TEXT,
    p_nouveau_contenu TEXT
)
RETURNS VOID AS $$
DECLARE
v_user_id INTEGER;
BEGIN
BEGIN
        v_user_id := current_setting('app.current_user_id', true)::INTEGER;
EXCEPTION
        WHEN OTHERS THEN
            v_user_id := NULL;
END;

INSERT INTO log(
    table_name,
    operation,
    date_action,
    ancien_contenu,
    nouveau_contenu,
    id_utilisateur
)
VALUES(
          p_table_name,
          p_operation,
          NOW(),
          p_ancien_contenu,
          p_nouveau_contenu,
          v_user_id
      );
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- SUPPRESSION ANCIENS TRIGGERS
-- =====================================================

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

-- =====================================================
-- LOG FRANCHISE : INSERT
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_franchise_create()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'franchise',
        'INSERT',
        '',
        'ID: ' || NEW.id_franchise ||
        ', Nom: ' || NEW.nom_franchise ||
        ', Siège: ' || COALESCE(NEW.siege_social, '') ||
        ', ID gérant: ' || COALESCE(NEW.id_gerant::TEXT, 'aucun')
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER franchise_create
    AFTER INSERT ON franchise
    FOR EACH ROW EXECUTE FUNCTION trigger_franchise_create();

-- =====================================================
-- LOG FRANCHISE : UPDATE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_franchise_update()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'franchise',
        'UPDATE',
        'ID: ' || OLD.id_franchise ||
        ', Nom: ' || OLD.nom_franchise ||
        ', Siège: ' || COALESCE(OLD.siege_social, '') ||
        ', ID gérant: ' || COALESCE(OLD.id_gerant::TEXT, 'aucun'),

        'ID: ' || NEW.id_franchise ||
        ', Nom: ' || NEW.nom_franchise ||
        ', Siège: ' || COALESCE(NEW.siege_social, '') ||
        ', ID gérant: ' || COALESCE(NEW.id_gerant::TEXT, 'aucun')
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER franchise_update
    AFTER UPDATE ON franchise
    FOR EACH ROW EXECUTE FUNCTION trigger_franchise_update();

-- =====================================================
-- LOG FRANCHISE : DELETE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_franchise_delete()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'franchise',
        'DELETE',
        'ID: ' || OLD.id_franchise ||
        ', Nom: ' || OLD.nom_franchise ||
        ', Siège: ' || COALESCE(OLD.siege_social, '') ||
        ', ID gérant: ' || COALESCE(OLD.id_gerant::TEXT, 'aucun'),
        ''
    );
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER franchise_delete
    AFTER DELETE ON franchise
    FOR EACH ROW EXECUTE FUNCTION trigger_franchise_delete();

-- =====================================================
-- LOG CINEMA : INSERT
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_cinema_create()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'cinema',
        'INSERT',
        '',
        'ID: ' || NEW.id_cinema ||
        ', Dénomination: ' || NEW.denomination ||
        ', Adresse: ' || COALESCE(NEW.adresse, '') ||
        ', Ville: ' || COALESCE(NEW.ville, '') ||
        ', ID franchise: ' || NEW.id_franchise
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cinema_create
    AFTER INSERT ON cinema
    FOR EACH ROW EXECUTE FUNCTION trigger_cinema_create();

-- =====================================================
-- LOG CINEMA : UPDATE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_cinema_update()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'cinema',
        'UPDATE',
        'ID: ' || OLD.id_cinema ||
        ', Dénomination: ' || OLD.denomination ||
        ', Adresse: ' || COALESCE(OLD.adresse, '') ||
        ', Ville: ' || COALESCE(OLD.ville, '') ||
        ', ID franchise: ' || OLD.id_franchise,

        'ID: ' || NEW.id_cinema ||
        ', Dénomination: ' || NEW.denomination ||
        ', Adresse: ' || COALESCE(NEW.adresse, '') ||
        ', Ville: ' || COALESCE(NEW.ville, '') ||
        ', ID franchise: ' || NEW.id_franchise
    );
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cinema_update
    AFTER UPDATE ON cinema
    FOR EACH ROW EXECUTE FUNCTION trigger_cinema_update();

-- =====================================================
-- LOG CINEMA : DELETE
-- =====================================================

CREATE OR REPLACE FUNCTION trigger_cinema_delete()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM insert_log_function(
        'cinema',
        'DELETE',
        'ID: ' || OLD.id_cinema ||
        ', Dénomination: ' || OLD.denomination ||
        ', Adresse: ' || COALESCE(OLD.adresse, '') ||
        ', Ville: ' || COALESCE(OLD.ville, '') ||
        ', ID franchise: ' || OLD.id_franchise,
        ''
    );
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cinema_delete
    AFTER DELETE ON cinema
    FOR EACH ROW EXECUTE FUNCTION trigger_cinema_delete();