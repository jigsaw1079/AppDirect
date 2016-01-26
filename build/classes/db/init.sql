CREATE TABLE IF NOT EXISTS company(
	uuid char(36) primary key,
	country varchar(20),
	email varchar(50),
	name varchar(50),
	phone char(12),
	website varchar(100)
);

CREATE TABLE IF NOT EXISTS individual(
	uuid char(36) primary key,
	fname varchar(30),
	lname varchar(30),
	language varchar(10),
	email varchar(50)
);

CREATE TABLE IF NOT EXISTS orders(
	accountId char(50) primary key,
	cre_uuid char(36),
	lastModifiedTime long,
	edition varchar(20),
	pricingDuration varchar(20),
	status varchar(20)
);

CREATE TABLE IF NOT EXISTS order_item(
	accountId char(50),
	unit varchar(20),
	quantity int,
	primary key(accountId, unit)
);

CREATE TABLE IF NOT EXISTS order_assignment(
	accountId char(50),
	cre_uuid char(36),
	usr_uuid char(36),
	entryKey varchar(20),
	entryValue varchar(30),
	primary key(accountId, usr_uuid, entryKey)
);

	