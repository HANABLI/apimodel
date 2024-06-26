--#######################################################
--###                                                 ###
--### Author: NABLI Hatem                             ###
--### License: NONE                                   ###
--### DATE: 15/02/2024                                ###
--### VERSION: 1.0                                    ###
--###                                                 ###
--#######################################################

--/*
-- * ---- General Rules ---
-- * Use underscore_names instead  for camelCase
-- * Table names should be plural
-- * Spell out id fields (item_id instead of id)
-- * Don't use ambiguous colum names
-- * Name foreign key columns the same as the columns they refer to
-- * Use caps for all SQL queries
-- */

CREATE TABLE IF NOT EXISTS api_keys (
    apikey TEXT NOT NULL,
    user_id TEXT NOT NULL,
    subscription TEXT NOT NULL,

    CONSTRAINT api_keys_pk PRIMARY KEY (apikey)
);

CREATE INDEX api_keys_apikey_idx ON api_keys (apikey);

CREATE TABLE IF NOT EXISTS todo_lists (
    user_id  TEXT NOT NULL ,
    id       TEXT NOT NULL ,
    name     TEXT NOT NULL ,

    CONSTRAINT todo_lists_pk PRIMARY KEY (user_id, id)
);

CREATE UNIQUE INDEX todo_lists_user_id_id_idx ON todo_lists (user_id, id);
CREATE INDEX todo_lists_user_id_idx ON todo_lists (user_id);
CREATE INDEX todo_lists_id_idx ON todo_lists (id);

CREATE TABLE IF NOT EXISTS todo_items (
    user_id TEXT NOT NULL,
    list_id TEXT NOT NULL,
    id      TEXT NOT NULL,
    task    TEXT NOT NULL,
    done    BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT todo_items_pk PRIMARY KEY (user_id, list_id, id),
    CONSTRAINT todo_items_fk_user_id_list_id FOREIGN KEY (user_id, list_id)
        REFERENCES todo_lists (user_id, id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE UNIQUE INDEX todo_items_user_id_list_id_id_idx ON todo_items (user_id, list_id, id);
CREATE INDEX todo_items_user_id_idx ON todo_items (user_id);
CREATE INDEX todo_items_list_id_idx ON todo_items (list_id);
CREATE INDEX todo_items_id_idx ON todo_items (id);
