drop table task_result;

create table task_result (
    id serial PRIMARY KEY,
    task_id int not null,
    result  integer[],
    successful boolean,
    message varchar(255),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
