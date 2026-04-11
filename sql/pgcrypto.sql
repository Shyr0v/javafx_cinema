
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO utilisateur (nom, prenom, login, mdp)
VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', crypt('jean', gen_salt('bf'))),
('Martin', 'Alice', 'alice.martin@email.com', crypt('alice', gen_salt('bf'))),
('Bernard', 'Lucas', 'lucas.bernard@email.com', crypt('lucas', gen_salt('bf')));