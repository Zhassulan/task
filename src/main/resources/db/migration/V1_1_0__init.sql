drop table if exists task_result;

create table task_result (
    id serial PRIMARY KEY,
    request_id uuid not null,
    task_id int not null,
    result  integer[],
    successful boolean,
    message varchar(255),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
