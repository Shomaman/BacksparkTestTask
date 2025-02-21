create table sock(
id serial primary key,
sock_color varchar(200) not null,
cotton int not null,
quantity int not null,
constraint unique_sock_color_cotton unique (sock_color, cotton)
);