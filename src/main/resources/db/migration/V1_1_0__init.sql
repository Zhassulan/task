DO $$
BEGIN
    IF NOT EXISTS (
        -- Query the system catalog to check for the database
        SELECT 1 FROM pg_database WHERE datname = 'tasks'
    ) THEN
        -- Execute the CREATE DATABASE statement
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE tasks');
    END IF;
END $$;
