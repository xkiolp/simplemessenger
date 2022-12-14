CREATE TABLE `friend` (
  `user1` varchar(15) NOT NULL,
  `user2` varchar(15) NOT NULL,
  PRIMARY KEY (`user1`,`user2`),
  KEY `user2` (`user2`),
  CONSTRAINT `friend_ibfk_1` FOREIGN KEY (`user1`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `friend_ibfk_2` FOREIGN KEY (`user2`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE TABLE `user` (
  `id` varchar(15) NOT NULL,
  `nickname` varchar(15) DEFAULT NULL,
  `name` varchar(15) DEFAULT NULL,
  `email` varchar(20) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `pw` varchar(200) DEFAULT NULL,
  `ip` varchar(15) DEFAULT NULL,
  `portnum` int DEFAULT NULL,
  `salt` varchar(200) DEFAULT NULL,
  `todayMessage` varchar(50) DEFAULT NULL,
  `profileImage` mediumblob,
  PRIMARY KEY (`id`)
);