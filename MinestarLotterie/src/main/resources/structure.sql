CREATE TABLE IF NOT EXISTS `stakes` (
	`player` varchar(32) PRIMARY KEY,
	`number` INTEGER NOT NULL DEFAULT '0'
);

CREATE TABLE IF NOT EXISTS `winner` (
	`player` varchar(32) PRIMARY KEY,
	`value` INTEGER NOT NULL DEFAULT '0'
);

CREATE TABLE IF NOT EXISTS `draws` (
	`time` LONG PRIMARY KEY,
	`number` INTEGER NOT NULL DEFAULT '0',
	`auto` BOOLEAN DEFAULT TRUE,
	`array` varchar(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS `nextdraw` (
	`id` INTEGER PRIMARY KEY,
	`time` LONG NOT NULL DEFAULT '0',
	`prize` INTEGER NOT NULL DEFAULT '0'
);

CREATE TABLE IF NOT EXISTS `winnerarrays` (
	`arrayname` varchar(32) NOT NULL,
	`playername` varchar(32) NOT NULL
);