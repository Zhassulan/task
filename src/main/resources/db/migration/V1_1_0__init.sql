drop table if exists task_result;

create table task_result (
    id serial PRIMARY KEY,
    request_id uuid not null,
    task_id int not null,
    `min` int not null,
    `max` int not null,
    `count` int not null,
    result  integer[],
    successful boolean default false,
    message varchar(255),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE UNIQUE INDEX IDX_REQ_ID ON task_result(request_id);
CREATE INDEX IDX_REQ_ID ON task_result(successful);
