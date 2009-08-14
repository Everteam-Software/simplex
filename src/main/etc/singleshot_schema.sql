DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities` (
  `id` int(11) NOT NULL auto_increment,
  `person_id` int(11) NOT NULL,
  `task_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

INSERT INTO `activities` VALUES (1,1,1,'task.created','2009-05-27 07:08:48'),(2,1,2,'task.created','2009-05-27 07:53:48'),(3,1,2,'task.claimed','2009-05-27 08:37:48'),(4,1,5,'task.claimed','2009-05-27 11:06:48'),(5,2,6,'task.claimed','2009-05-27 13:07:48'),(6,1,7,'task.claimed','2009-05-27 14:01:48'),(7,1,8,'task.claimed','2009-05-27 14:48:48'),(8,1,9,'task.claimed','2009-05-27 15:24:49'),(9,1,10,'task.claimed','2009-05-27 16:24:49'),(10,2,11,'task.suspended','2009-05-27 18:31:49'),(11,1,12,'task.claimed','2009-05-27 19:32:49'),(12,1,12,'task.completed','2009-05-27 20:12:49'),(13,1,13,'task.cancelled','2009-05-27 21:50:49');

DROP TABLE IF EXISTS `forms`;
CREATE TABLE `forms` (
  `id` int(11) NOT NULL auto_increment,
  `task_id` int(11) NOT NULL,
  `url` varchar(255) default NULL,
  `html` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

INSERT INTO `forms` VALUES (1,1,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(2,2,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(3,3,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(4,4,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(5,5,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(6,6,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(7,7,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(8,8,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(9,9,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(10,10,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(11,11,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(12,12,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(13,13,NULL,'  <p>{{ owner.fullname }}, please update your contact info:</p>\n  <dl>\n    <dt>Phone:</dt><dd><input name=\'data[phone]\' size=\'40\' type=\'text\'></dd>\n    <dt>Address:</dt><dd><textarea name=\'data[address]\' cols=\'40\' rows=\'4\'></textarea></dd>\n    <dt>E-mail:</dt><dd><input name=\'data[email]\' size=\'40\' type=\'text\'></dd>\n    <dt>D.O.B:</dt><dd><input name=\'data[dob]\' type=\'text\' class=\'date\'></dd>\n  </dl>\n'),(14,14,NULL,"<dl><dt>From: </dt><dd><input name='data[from]' type='text' class='date'/></dd><dt>To: </dt><dd><input name='data[to]' type='text' class='date'/></dd></dl>");

DROP TABLE IF EXISTS `notification_copies`;
CREATE TABLE `notification_copies` (
  `id` int(11) NOT NULL auto_increment,
  `notification_id` int(11) NOT NULL,
  `recipient_id` int(11) NOT NULL,
  `marked_read` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `index_notification_copies_on_notification_id_and_recipient_id` (`notification_id`,`recipient_id`),
  KEY `index_notification_copies_on_recipient_id_and_read` (`recipient_id`,`marked_read`)
) ENGINE=InnoDB;

INSERT INTO `notification_copies` VALUES (1,1,1,0),(2,2,1,0),(3,3,1,0),(4,4,1,0),(5,5,1,0);

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` int(11) NOT NULL auto_increment,
  `subject` varchar(200) NOT NULL,
  `body` varchar(4000) default NULL,
  `language` varchar(5) default NULL,
  `creator_id` int(11) default NULL,
  `task_id` int(11) default NULL,
  `priority` tinyint(4) NOT NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

INSERT INTO `notifications` VALUES (1,'Nesciunt laudantium quam molestiae assumenda consequatur voluptatem aut autem.','Sed placeat unde odio et ipsum voluptatum quia officiis. Doloribus ab atque dolorum quia et iusto aliquam. Eos consectetur repellendus harum tempora sed. Quis sunt molestias qui quo. Sunt facere exercitationem id voluptatem.\n\nAnimi explicabo tempore assumenda sit sint id voluptas non. Voluptatem labore commodi sed tempore non unde totam molestiae. Ut aut sed molestiae tempora. Quidem sunt velit reprehenderit deleniti eveniet qui aut. Et alias tenetur veniam cupiditate tempora occaecati suscipit blanditiis.\n\nAd sunt quidem placeat vel est. Accusamus atque debitis repellat aut neque veritatis voluptatem. Esse error optio odio.\n\nQui sit quia libero. Nostrum ut omnis beatae et expedita ipsam quo. Architecto illo voluptas nostrum consequuntur corporis ipsam veritatis et. Officiis tenetur ducimus doloremque.\n\nLaboriosam est nulla possimus in ut maxime quidem eius. Recusandae iste sit ut ut repellendus molestiae. Mollitia cum vero saepe. Sed consectetur corrupti id. Autem eaque quos cupiditate veniam beatae dolor rerum.',NULL,NULL,NULL,2,'2009-05-27 21:50:49','2009-05-27 21:50:49'),(2,'Commodi architecto velit quod assumenda ducimus rerum est numquam.','Iusto modi sapiente culpa aut quibusdam. Aut sit repudiandae vero perferendis cum inventore ea. Minima et rerum sit ipsum et. Ea et non qui occaecati quia possimus aut.\n\nBeatae dolores eaque illum ut illo et quos. Dicta perspiciatis eos consequatur facere libero accusantium. Aliquam veritatis fuga sunt sit. Commodi qui sed repellendus.\n\nVoluptate libero voluptas aut sed voluptatibus aspernatur. Beatae assumenda at ea esse dolorem qui aliquid. Accusantium sed repellendus voluptates corrupti et dolor aut. Odio facere possimus id ex beatae molestias quia dolor. Aut et eum aut molestias voluptatem reprehenderit excepturi soluta.\n\nSapiente magnam reiciendis iusto earum. Velit consequatur dolor omnis blanditiis eius ea eum. Aut facere explicabo pariatur voluptatem suscipit nostrum ad. Saepe et fugit perferendis optio qui eum et. Alias molestiae eum temporibus quis.\n\nIste aut facere eveniet aut quaerat cupiditate. Omnis repellendus soluta iusto quia nisi. Sint aliquam non consectetur.',NULL,NULL,NULL,2,'2009-05-27 21:50:49','2009-05-27 21:50:49'),(3,'Porro quidem aliquam enim esse et.','Nostrum quasi vel architecto. Placeat mollitia earum omnis aut enim est ut. Non ipsum ullam sit id fugiat voluptates et. Rerum omnis fugiat omnis commodi rem nihil fuga repudiandae. Rerum dolorem ea odit est dolor rem provident.\n\nEligendi commodi eum itaque quod voluptates aliquid non. Deleniti inventore nemo perspiciatis nostrum. Numquam quis est nulla voluptatem quo quaerat hic asperiores.\n\nVitae voluptatem repudiandae tempora temporibus accusantium velit. Ipsum temporibus ut aspernatur est occaecati similique. Culpa ea quibusdam explicabo architecto in. Non reprehenderit corrupti necessitatibus. Animi sunt distinctio et voluptatibus facere praesentium earum sint.\n\nConsequatur expedita aliquid est tempora quos qui. Rerum tenetur voluptas at. Vitae et eum maxime voluptas.\n\nEligendi et pariatur perferendis error sit atque quia deserunt. Voluptatem qui illum cumque quia reprehenderit aperiam laboriosam. Consectetur quaerat ut quo voluptatem et velit expedita repellendus. Fugiat porro qui ullam. Et quasi aliquid consequuntur suscipit ducimus aut.',NULL,NULL,4,2,'2009-05-27 21:50:50','2009-05-27 21:50:50'),(4,'Odit eum delectus vero quaerat praesentium.','Aperiam sunt unde quis ut velit maiores. Qui placeat velit assumenda. Iusto aut mollitia accusantium accusamus quis. Sunt ipsa architecto corporis aliquam explicabo nulla tenetur.\n\nOmnis et aspernatur aliquam. In ut doloribus velit laudantium explicabo neque. Rem eveniet quod omnis illum et libero quis voluptate. Totam tenetur sed tempora non ducimus voluptatibus.\n\nAccusantium quidem et pariatur dicta. Repellendus qui deleniti est dignissimos consequuntur. Qui neque est amet est ullam deserunt aut.\n\nConsequatur cumque corrupti enim voluptatibus dolorum quasi sunt assumenda. Voluptatem est necessitatibus a vel dolore quod soluta sunt. Enim blanditiis maiores facilis incidunt delectus. Mollitia et harum laborum. Omnis aut nostrum nihil quidem expedita.\n\nCorrupti repellat ex quisquam omnis. Rerum dignissimos voluptatibus accusantium dolor. Consequuntur dolor illo adipisci voluptas delectus maxime. Est dolorem soluta aliquam ea expedita accusantium et qui.',NULL,NULL,NULL,2,'2009-05-27 21:50:51','2009-05-27 21:50:51'),(5,'Veritatis illum laboriosam deleniti explicabo facilis.','Rerum et dolores illum ea ut. Et sed dolorem et autem cupiditate enim ut. Quidem est voluptatem ut quisquam quod ipsa. Et ratione sint non eum ad dolorum.\n\nIpsam dolores reprehenderit id fugiat. Quasi a consectetur et dicta ratione cumque minus. Ducimus illo quos maxime voluptatibus amet. Deserunt rem ut quo minima voluptatem dolor optio id. Repellendus et recusandae ratione reiciendis laborum officiis nisi.\n\nIusto unde a reiciendis saepe quis nihil corrupti. Sint repellat rerum est incidunt. Animi fugiat omnis possimus. Dolore alias accusantium omnis nam consectetur itaque eos.\n\nAt magnam dolor qui laudantium voluptas. Voluptas velit sed commodi nobis eligendi. Earum rerum aut rerum. Enim sit maxime qui officiis a. Et voluptate facilis aliquam voluptatem qui quia omnis.\n\nFugiat est a iusto omnis aut. Et aut repudiandae distinctio voluptates doloribus vitae. Perspiciatis quia maiores nam aperiam quia enim. Consectetur commodi excepturi quidem sint similique nobis fuga harum. Consequatur dignissimos quo quod.',NULL,NULL,NULL,2,'2009-05-27 21:50:51','2009-05-27 21:50:51');
DROP TABLE IF EXISTS `people`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `people` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `locale` varchar(5) DEFAULT NULL,
  `timezone` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `login` varchar(255) NOT NULL,
  `crypted_password` varchar(255) NOT NULL,
  `password_salt` varchar(255) NOT NULL,
  `persistence_token` varchar(255) NOT NULL,
  `single_access_token` varchar(255) NOT NULL,
  `perishable_token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_people_on_email` (`email`),
  KEY `index_people_on_fullname` (`fullname`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

INSERT INTO `people` VALUES (1,'Singleshot','singleshot@example.com', NULL, NULL, '2009-08-14 22:04:16', '2009-08-14 22:04:16', 'singleshot', '1ae325c4c42ba9744bd0ec2b32be594b77d6f734519d9eafb5b29dcd0cd6ef3a3045b13b8c31bd6680e2d877d687c7d008f0aa6ade4f4408c62e490a77f36ab8', 'PSUyUJi1kC4sxV38k7yb', '94b01c2f76cbbf11b4250942a602e67bfd0468b5163b7fd5da3284d16275bb8c48df155ae88b5cdae81f55f50f3797ba92de5561dd5c48fd159d317777193ea4', 'UW-xwf7ceAgYVK6fIc61', '08pXv9qj5h4ow4GRi4Ip'), (2, 'Mr.Bond', 'bond@example.com', NULL, NULL, '2009-08-14 22:04:16', '2009-08-14 22:24:04', 'bond', '42fe37c4f57eb1d2c6d26ecda9f4d426727cbabb014727b970bf901bf50950efd0a269c269f888e12820546e7347ab9555ba7e159a66c249f35529b05f82806a', 'ydEd1j5W_t4Sh2RuVV5W', '17d4f60c59aa91d2a03e5f1f87f99c6f331571fd9db8a285a73243c7f7ed26ff7a039a75e2bae54eb0872919e44b641c9a6f2b4b257468c9fc368fb19039f729', '5_0nq3VsMMjRfm_T5OPx', '8JCnr2ZWzCxD0FOUX1fZ');

DROP TABLE IF EXISTS `schema_migrations`;
CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB;

INSERT INTO `schema_migrations` VALUES ('20090121215854'),('20090121220044'),('20090206215123'),('20090402190432'),('20090421005807'),('20090508224047'),('20090521154442');

DROP TABLE IF EXISTS `sessions`;
CREATE TABLE `sessions` (
  `id` int(11) NOT NULL auto_increment,
  `session_id` varchar(255) NOT NULL,
  `data` text,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `index_sessions_on_session_id` (`session_id`),
  KEY `index_sessions_on_updated_at` (`updated_at`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `stakeholders`;
CREATE TABLE `stakeholders` (
  `id` int(11) NOT NULL auto_increment,
  `person_id` int(11) NOT NULL,
  `task_id` int(11) NOT NULL,
  `role` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `index_stakeholders_on_person_id_and_task_id_and_role` (`person_id`,`task_id`,`role`)
) ENGINE=InnoDB;

INSERT INTO `stakeholders` VALUES (1,1,1,'creator','2009-05-27 21:50:48'),(2,1,1,'potential_owner','2009-05-27 21:50:48'),(3,2,1,'potential_owner','2009-05-27 21:50:48'),(4,1,2,'creator','2009-05-27 21:50:48'),(5,1,2,'potential_owner','2009-05-27 21:50:48'),(6,2,2,'potential_owner','2009-05-27 21:50:48'),(7,1,2,'owner','2009-05-27 21:50:48'),(8,1,3,'potential_owner','2009-05-27 21:50:48'),(9,2,3,'potential_owner','2009-05-27 21:50:48'),(10,1,3,'observer','2009-05-27 21:50:48'),(11,1,4,'potential_owner','2009-05-27 21:50:48'),(12,2,4,'potential_owner','2009-05-27 21:50:48'),(13,1,4,'supervisor','2009-05-27 21:50:48'),(14,1,5,'potential_owner','2009-05-27 21:50:48'),(15,1,5,'owner','2009-05-27 21:50:48'),(16,1,6,'potential_owner','2009-05-27 21:50:48'),(17,2,6,'potential_owner','2009-05-27 21:50:48'),(18,2,6,'owner','2009-05-27 21:50:48'),(19,1,7,'owner','2009-05-27 21:50:48'),(20,1,7,'potential_owner','2009-05-27 21:50:48'),(21,2,7,'potential_owner','2009-05-27 21:50:48'),(22,1,8,'owner','2009-05-27 21:50:48'),(23,1,8,'potential_owner','2009-05-27 21:50:48'),(24,2,8,'potential_owner','2009-05-27 21:50:48'),(25,1,9,'owner','2009-05-27 21:50:49'),(26,1,9,'potential_owner','2009-05-27 21:50:49'),(27,2,9,'potential_owner','2009-05-27 21:50:49'),(28,1,10,'owner','2009-05-27 21:50:49'),(29,1,10,'potential_owner','2009-05-27 21:50:49'),(30,2,10,'potential_owner','2009-05-27 21:50:49'),(31,1,11,'potential_owner','2009-05-27 21:50:49'),(32,2,11,'potential_owner','2009-05-27 21:50:49'),(33,2,11,'supervisor','2009-05-27 21:50:49'),(34,1,12,'owner','2009-05-27 21:50:49'),(35,1,12,'potential_owner','2009-05-27 21:50:49'),(36,2,12,'potential_owner','2009-05-27 21:50:49'),(37,1,13,'potential_owner','2009-05-27 21:50:49'),(38,2,13,'potential_owner','2009-05-27 21:50:49'),(39,1,13,'supervisor','2009-05-27 21:50:49'),(40,1,14,'potential_owner','2009-05-27 21:50:49');

DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `id` int(11) NOT NULL auto_increment,
  `status` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `language` varchar(5) default NULL,
  `priority` tinyint(4) NOT NULL,
  `due_on` date default NULL,
  `start_on` date default NULL,
  `cancellation` varchar(255) default NULL,
  `data` text NOT NULL,
  `hooks` varchar(255) default NULL,
  `access_key` varchar(32) default NULL,
  `version` int(11) default NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

INSERT INTO `tasks` VALUES (1,'available','Voluptatem eveniet possimus fugit.','Minus fugit odit qui necessitatibus labore ipsam quia. Sunt deleniti inventore eum est aliquam autem placeat sed. Deserunt rem voluptatum vel corrupti asperiores est alias quod. Totam quos voluptatem fugit iste doloremque.\n\nEaque repellendus harum et quo ',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'baf950c81aa281e2180643eab6ec5d22',0,'2009-05-27 07:08:48','2009-05-27 07:08:48','Task'),(2,'active','Et voluptas quia nesciunt ab esse modi voluptas.','Sunt pariatur nihil ratione voluptatum ex cum dolor. Cupiditate provident sequi sint quia eos ipsam officia quod. Ut dolores nihil aperiam accusantium natus voluptate ex. Et molestiae debitis aut ut a eveniet nostrum.\n\nNecessitatibus reprehenderit commodi',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'1531fb4db0226470ceb8fe6e8cd9b796',1,'2009-05-27 07:53:48','2009-05-27 08:37:48','Task'),(3,'available','Pariatur fuga aliquam recusandae.','Sit voluptatem dolor earum necessitatibus. Vero in dolore culpa perferendis. Recusandae quidem cumque maiores optio quis quisquam ratione. Consectetur in animi consequatur sequi voluptatem maiores sint sint.\n\nAutem perferendis est et commodi cupiditate qu',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'9f9c615bf890cff9346e44e6c0099e35',0,'2009-05-27 09:42:48','2009-05-27 09:42:48','Task'),(4,'available','Commodi quasi consequuntur at ut aut.','Mollitia quisquam nobis eos in sed dolore id quis. Ut voluptates ut error dignissimos voluptas expedita. Dolore dignissimos possimus et odit ratione sunt sit accusamus. Culpa at consequuntur dignissimos tempora hic delectus.\n\nAut ex saepe facere iure. Asp',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'7d1dab5d91992c006cfad150b234bc7b',0,'2009-05-27 10:19:48','2009-05-27 10:19:48','Task'),(5,'available','Enim est porro in unde.','Nulla et dolor dolorem sint praesentium rerum itaque eum. Officia consequuntur qui modi distinctio. Hic at quod dignissimos aliquid. Perspiciatis earum qui odit ratione.\n\nRem nostrum dolores dolorem animi ipsum ipsa atque. Voluptatem eligendi quibusdam re',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'b154ef709ad8aa894d8c95676d4e31c7',0,'2009-05-27 11:06:48','2009-05-27 11:06:48','Task'),(6,'active','Alias sed odio placeat explicabo blanditiis consequatur earum mollitia.','Quisquam repudiandae quia blanditiis tenetur labore aut amet. Soluta enim ullam est qui et est. Non iste voluptas ut. Et aliquid ipsum officia quia.\n\nConsequatur dolores dicta qui qui soluta. Aliquid culpa enim sequi ut. Asperiores perspiciatis nesciunt p',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'79e59e43b03bb9ea22f3c4f19e4adce6',1,'2009-05-27 12:00:48','2009-05-27 13:07:48','Task'),(7,'active','Possimus voluptatem repellendus odit maiores nostrum aut.','Facilis quaerat neque ea molestias ut. Rerum consectetur harum magnam. Qui quasi quo harum. Error vitae eaque ad natus voluptatem incidunt distinctio. Ea qui et in.\n\nIpsum incidunt neque consequatur magni ut autem. Aut omnis harum et odit iusto. Recusanda',NULL,1,NULL,NULL,NULL,'--- {}\n\n',NULL,'8ce873b57ba3062868102de3546c5d39',0,'2009-05-27 14:01:48','2009-05-27 14:01:48','Task'),(8,'active','Voluptatum rerum officiis adipisci ut consectetur beatae.','Qui magni veniam reiciendis beatae amet sapiente. Et dolor numquam quia tenetur culpa. Aut non et ut accusamus iste ab aperiam.\n\nOptio atque exercitationem quia soluta tempore sequi. Perspiciatis quas corrupti nobis in vero rem vel. Aut qui commodi iste. ',NULL,2,'2009-05-26',NULL,NULL,'--- {}\n\n',NULL,'a7c31cbd717b873f4e7bb30e08e44774',0,'2009-05-27 14:48:48','2009-05-27 14:48:48','Task'),(9,'active','Nihil adipisci vel et tempore in voluptates qui nihil.','Totam suscipit veritatis cupiditate. Neque quisquam odit voluptas est explicabo quam. Odio et eos mollitia natus labore in magni. Ullam quia nostrum consequatur aliquid.\n\nSed excepturi aut optio rerum. Dolor labore odit dignissimos exercitationem a sint q',NULL,2,'2009-05-27',NULL,NULL,'--- {}\n\n',NULL,'c6ff25b1b6af44deeb5354b1eb8b6578',0,'2009-05-27 15:24:49','2009-05-27 15:24:49','Task'),(10,'active','Quis reprehenderit sunt et ullam.','Quia rerum ratione et delectus. Culpa rerum error tenetur ut alias. Ut maiores ab excepturi voluptas. Corporis sed sit et.\n\nEt ut eius sit neque. Asperiores officia error laborum. Voluptatem rerum ab eum. Officia veritatis aperiam accusamus voluptatem sap',NULL,2,'2009-05-28',NULL,NULL,'--- {}\n\n',NULL,'8ec64ec26a627f8af3a2c39ac440c1fe',0,'2009-05-27 16:24:49','2009-05-27 16:24:49','Task'),(11,'suspended','Est blanditiis ut velit quia non sed.','Quibusdam illo tenetur quasi vel deserunt provident et quam. Voluptas et voluptatem saepe laudantium est rerum qui. Fuga aut earum eum.\n\nHarum repudiandae delectus qui et eligendi dolorem. In officia non temporibus explicabo eius quia ut. Commodi sapiente',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'511a884356db01cb3c952c69e657a342',1,'2009-05-27 17:25:49','2009-05-27 18:31:49','Task'),(12,'completed','Aut accusantium cum voluptas molestias iste in nihil.','Ad id animi nobis consequatur iusto pariatur ut facilis. Inventore vitae iusto fuga aliquid eum voluptas libero sunt. Non provident aliquid suscipit et. Eum et deserunt ad repellendus exercitationem occaecati et sed.\n\nEt cupiditate alias non. Aut dolores ',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'0040133c3bc20e456c54dba88f10ca82',1,'2009-05-27 19:32:49','2009-05-27 20:12:49','Task'),(13,'cancelled','Commodi et voluptatem veniam atque repellat saepe aut.','Ullam fuga nesciunt ut quas occaecati animi. Eum pariatur molestiae repellendus sunt quasi. Mollitia rem eos animi. Itaque ipsum fuga assumenda nulla.\n\nConsequuntur ratione eos sit iste labore. Iste inventore eos rem voluptas sapiente. Aut enim doloribus ',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,'3002ee7f6579483928356c34e103b684',1,'2009-05-27 21:50:49','2009-05-27 21:50:49','Task'),(14,'enabled','Absence request','Request leave of absence',NULL,2,NULL,NULL,NULL,'--- {}\n\n',NULL,NULL,NULL,'2009-05-27 21:50:49','2009-05-27 21:50:49','Template');

DROP TABLE IF EXISTS `webhooks`;
CREATE TABLE `webhooks` (
  `id` int(11) NOT NULL auto_increment,
  `task_id` int(11) NOT NULL,
  `event` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `http_method` varchar(255) NOT NULL,
  `enctype` varchar(255) NOT NULL,
  `hmac_key` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

insert into webhooks (task_id, event, url, http_method, enctype) values (14, 'completed', 'http://localhost:3434/absence', 'post', 'application/xml');

