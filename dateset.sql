/*
Navicat MySQL Data Transfer

Source Server         : 2333
Source Server Version : 80026
Source Host           : 121.37.20.146:3306
Source Database       : allchat

Target Server Type    : MYSQL
Target Server Version : 80026
File Encoding         : 65001

Date: 2022-08-16 08:34:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `Friend`
-- ----------------------------
DROP TABLE IF EXISTS `Friend`;
CREATE TABLE `Friend` (
  `user` varchar(255) NOT NULL,
  `friendUser` varchar(255) NOT NULL,
  KEY `user` (`user`),
  KEY `friendUser` (`friendUser`),
  CONSTRAINT `Friend_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Friend_ibfk_2` FOREIGN KEY (`friendUser`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of Friend
-- ----------------------------
INSERT INTO `Friend` VALUES ('abcde233', 'qwert233');
INSERT INTO `Friend` VALUES ('qwert233', 'abcde233');

-- ----------------------------
-- Table structure for `Message`
-- ----------------------------
DROP TABLE IF EXISTS `Message`;
CREATE TABLE `Message` (
  `Date` int NOT NULL,
  `Sender` varchar(255) NOT NULL,
  `User` varchar(255) NOT NULL,
  `Msg` varchar(255) NOT NULL,
  KEY `User` (`User`),
  KEY `Sender` (`Sender`),
  CONSTRAINT `Message_ibfk_1` FOREIGN KEY (`User`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Message_ibfk_2` FOREIGN KEY (`Sender`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of Message
-- ----------------------------

-- ----------------------------
-- Table structure for `PreFriend`
-- ----------------------------
DROP TABLE IF EXISTS `PreFriend`;
CREATE TABLE `PreFriend` (
  `Name` varchar(255) NOT NULL,
  `FriendName` varchar(255) NOT NULL,
  KEY `Name` (`Name`),
  KEY `FriendName` (`FriendName`),
  CONSTRAINT `PreFriend_ibfk_1` FOREIGN KEY (`Name`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `PreFriend_ibfk_2` FOREIGN KEY (`FriendName`) REFERENCES `user` (`User`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of PreFriend
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `User` varchar(255) NOT NULL,
  `PasswordJ` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`User`),
  KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('abcde233', 'd3cef10c8b3c84723ab2ae569c4ab58b', 'bba9a104-c0c5-46f0-8b12-0d6aa406a74a');
INSERT INTO `user` VALUES ('qwert233', '829733c781b3d564391689c9d3410a96', '21d021f8-76a9-4690-ab2e-1619ed35c07d');
