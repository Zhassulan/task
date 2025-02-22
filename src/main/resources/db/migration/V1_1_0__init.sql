create table task_result (
    id serial PRIMARY KEY,
    task_id int not null,
    result  integer[],
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
