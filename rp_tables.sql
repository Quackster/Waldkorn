/*
Navicat MySQL Data Transfer

Source Server         : AJ
Source Server Version : 50136
Source Host           : ragescape.org:3306
Source Database       : waldkorn

Target Server Type    : MYSQL
Target Server Version : 50136
File Encoding         : 65001

Date: 2010-02-27 15:31:33
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `rp_npcs`
-- ----------------------------
DROP TABLE IF EXISTS `rp_npcs`;
CREATE TABLE `rp_npcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `motto` varchar(100) NOT NULL,
  `figure` varchar(255) NOT NULL,
  `sex` char(1) NOT NULL,
  `health` tinyint(4) NOT NULL DEFAULT '100',
  `spaceid` int(11) NOT NULL,
  `spawn_x` smallint(6) NOT NULL,
  `spawn_y` smallint(6) NOT NULL,
  `walk` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=57 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of rp_npcs
-- ----------------------------
INSERT INTO `rp_npcs` VALUES ('1', 'Bob', 'hamtaro has not forgotten us', 'sd=001/0&hr=023/160,140,100&hd=001/179,149,127&ey=005/0&fc=001/179,149,127&bd=001/179,149,127&lh=001/179,149,127&rh=001/179,149,127&ch=001/255,255,255&ls=002/255,255,255&rs=002/255,255,255&lg=002/51,51,51&sh=002/255,255,255', 'M', '100', '2839', '5', '3', '5,2 6,2 5,3 6,3');
INSERT INTO `rp_npcs` VALUES ('2', 'Viki', 'I love it when you rub it', 'sd=001&sh=002/204,0,0&lg=001/215,175,125&ch=002/204,0,0&lh=001/215,175,125&rh=001/215,175,125&hd=001/215,175,125&ey=001&fc=001/215,175,125&hr=503/235,240,163&rs=002/204,0,0&ls=002/204,0,0&bd=001/215,175,125', 'F', '100', '0', '5', '25', '');
INSERT INTO `rp_npcs` VALUES ('3', 'Riki', 'Cool beans y0!', 'sd=001&sh=002/17,17,17&lg=005/255,17,131&ch=018/17,17,17&lh=001/230,200,162&rh=001/230,200,162&hd=001/230,200,162&ey=002&fc=001/230,200,162&hr=202/165,90,24&hrb=202/2,3,4&rs=003/17,17,17&ls=003/17,17,17&bd=001/230,200,162', 'F', '100', '0', '4', '25', '');
INSERT INTO `rp_npcs` VALUES ('56', 'Rokko', 'don\'t mess with me', 'sd=001/0&hr=0/255,255,255&hd=911/202,144,114&ey=003/0&fc=001/202,144,114&bd=001/202,144,114&lh=001/202,144,114&rh=001/202,144,114&ch=520/51,51,51&ls=002/51,51,51&rs=002/51,51,51&lg=002/255,255,255&sh=003/47,45,38', 'M', '84', '2839', '8', '5', '8,6 8,7 7,6 5,6 7,8 8,5');

-- ----------------------------
-- Table structure for `rp_npcs_gossip`
-- ----------------------------
DROP TABLE IF EXISTS `rp_npcs_gossip`;
CREATE TABLE `rp_npcs_gossip` (
  `npc_id` int(11) NOT NULL,
  `text` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of rp_npcs_gossip
-- ----------------------------
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'Hngg... I need... monkeys...');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'If you want something tasty, then ask me for the menu!');
INSERT INTO `rp_npcs_gossip` VALUES ('1', '*zzzz*');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'Enjoy your stay at The Duck\'s Boots!');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'So... I heard you are hungry? Ask for menu!');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'Don\'t hesistate to recommend us to your friends!');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'Whoopee, I feel like I\'m fixin\' to die!');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'I wonder where all those monkeys went...');
INSERT INTO `rp_npcs_gossip` VALUES ('1', 'What\'s up with all those creepy guys creeping around?');
INSERT INTO `rp_npcs_gossip` VALUES ('1', '... it\'s kinda, empty here!');
INSERT INTO `rp_npcs_gossip` VALUES ('56', 'bring... bring amberlampz!');
INSERT INTO `rp_npcs_gossip` VALUES ('56', 'i\'ll put my foot up your ass... grr...');
INSERT INTO `rp_npcs_gossip` VALUES ('56', '*yawn*');
INSERT INTO `rp_npcs_gossip` VALUES ('56', '*burp*');

-- ----------------------------
-- Table structure for `rp_npcs_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `rp_npcs_triggers`;
CREATE TABLE `rp_npcs_triggers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `npc_id` int(11) NOT NULL,
  `words` text NOT NULL,
  `replies` text NOT NULL,
  `serve_item` varchar(100) DEFAULT NULL,
  `serve_replies` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of rp_npcs_triggers
-- ----------------------------
INSERT INTO `rp_npcs_triggers` VALUES ('8', '1', 'monkey#monkeys', 'You got monkeys?!#Wait, did I hear \'monkey\'?#Man it\'s been ages since I\'ve touched a monkey! :(', null, null);
INSERT INTO `rp_npcs_triggers` VALUES ('4', '1', 'carrot#vegetable', 'Hold on, I\'ll get you a carrot!#One carrot? Sure.', 'eat: carrot', 'There you are! It\'s fresh and crispy! :)#Enjoy your carrot!#I wish I could keep it to myself, but oh well!');
INSERT INTO `rp_npcs_triggers` VALUES ('6', '1', 'menu', 'We have beer, icecream and carrots!', null, null);
INSERT INTO `rp_npcs_triggers` VALUES ('7', '1', 'beer', 'Yarr, beer it is!#Sure, we have got plenty of beer!#One beer coming up!', 'beer', 'Enjoy!#Enjoy your beer!#Don\'t be afraid to ask for more!#Beer is what we need, enjoy!');
INSERT INTO `rp_npcs_triggers` VALUES ('9', '1', 'icecream#icecream', 'Coming up!#Fresh from the fridge, enjoy!#Everyone loves icecream!', 'eat: ice-cream', 'Enjoy!#Enjoy your ice!#Here you are, vanilla icecream!');
INSERT INTO `rp_npcs_triggers` VALUES ('10', '1', 'bob', 'Yep, that\'s me!#I think I heard my name!#Bob is your man!', null, null);
INSERT INTO `rp_npcs_triggers` VALUES ('11', '1', 'rokko', 'Rokko? Sst, don\'t speak out loud!#Rokko is a weird man, I think he is homeless!#Rokko beat me up last week, he always get\'s in a fight with other people', null, null);
