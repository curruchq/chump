CREATE DATABASE IF NOT EXISTS `ser` DEFAULT CHARSET=utf8;

USE `ser`;

DROP TABLE IF EXISTS `ser`.`usr_preferences`;
CREATE TABLE  `ser`.`usr_preferences` (
  `id` int(11) NOT NULL auto_increment,
  `uuid` varchar(64) NOT NULL default '',
  `username` varchar(100) NOT NULL default '0',
  `domain` varchar(128) NOT NULL default '',
  `attribute` varchar(32) NOT NULL default '',
  `value` varchar(128) NOT NULL default '',
  `type` int(11) NOT NULL default '0',
  `modified` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `date_start` timestamp NOT NULL default '0000-00-00 00:00:00',
  `date_end` timestamp NOT NULL default '0000-00-00 00:00:00',
  `subscriber_id` int(10) unsigned NOT NULL COMMENT 'Links to id column in subscriber table',
  PRIMARY KEY  (`username`,`domain`,`attribute`,`value`,`type`,`date_start`,`date_end`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;