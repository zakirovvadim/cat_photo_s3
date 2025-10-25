-- таблица coordination
CREATE TABLE IF NOT EXISTS coordination (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            path TEXT NOT NULL,
                                            creation_date TEXT NOT NULL
);

-- таблица photo_metadata
CREATE TABLE IF NOT EXISTS photo_metadata (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                                              path TEXT NOT NULL,
                                              title TEXT,
                                              ext TEXT,
                                              creation_date_time TEXT NOT NULL,
                                              creation_date TEXT,
                                              coordination_id INTEGER,

                                              FOREIGN KEY (coordination_id)
    REFERENCES coordination(id)
    ON DELETE SET NULL
    );

-- индексы
CREATE INDEX IF NOT EXISTS idx_photo_metadata_creation_date_time
    ON photo_metadata (creation_date_time);

CREATE INDEX IF NOT EXISTS idx_photo_metadata_coordination_id
    ON photo_metadata (coordination_id);
