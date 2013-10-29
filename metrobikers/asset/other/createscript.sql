delimiter $$

CREATE TABLE `routepoints` (
  `id` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`routeid`),
  KEY `route` (`routeid`),
  CONSTRAINT `route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `routes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `latestupdate` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`,`userid`),
  KEY `user` (`userid`),
  CONSTRAINT `user` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `userpositions` (
  `userid` int(11) NOT NULL,
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`userid`),
  KEY `userpositions_user` (`userid`),
  CONSTRAINT `userpositions_user` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$
CREATE TABLE `usersonroutes` (
  `userid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  PRIMARY KEY (`userid`,`routeid`),
  CONSTRAINT `useronroutes_route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `useronroutes_user` FOREIGN KEY (`userid`) REFERENCES `userpositions` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mail` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `password` char(32) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `activationdate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mail_UNIQUE` (`mail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `validationkeys` (
  `userid` int(11) NOT NULL,
  `validationkey` char(36) NOT NULL,
  `datecreated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`),
  CONSTRAINT `FK_USER` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

delimiter $$

CREATE TABLE `trackings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `routeid` int(11) NOT NULL,
  `time` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`,`userid`,`routeid`),
  KEY `tracking_route` (`routeid`),
  CONSTRAINT `tracking_route` FOREIGN KEY (`routeid`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `trackingpoints` (
  `id` int(11) NOT NULL DEFAULT '0',
  `trackingid` int(11) NOT NULL,
  `time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lat` bigint(20) DEFAULT NULL,
  `lon` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`trackingid`),
  KEY `tracking_point_tracking` (`trackingid`),
  CONSTRAINT `tracking_point_tracking` FOREIGN KEY (`trackingid`) REFERENCES `trackings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


