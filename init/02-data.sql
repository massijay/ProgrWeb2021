INSERT INTO `users` (`id`, `username`, `password`, `email`, `created_at`, `updated_at`) VALUES
(1,	'massimiliano',	'1000:O/zy5Tlzn/QI4J4xl6STgQ==:Qnuz5fK8hy//vesYFA6Gb+C7O8pscjDO4HYb4RrsbfkEdUlIUGiemX6I6jbnF08Fli0E/V5rGC6qwIMqamhh4w==',	'm.cristarella@outlook.com',	'2022-09-11 19:15:56',	'2022-09-11 19:15:56');

INSERT INTO `sessions` (`token`, `user_id`, `expire_at`) VALUES
('75aee90a-d2d4-4feb-8cdc-f6549a800477',	1,	'2022-09-11 20:25:32');

INSERT INTO `trips` (`id`, `user_id`, `trip_name`, `trip_date`, `transport_type`, `notes`) VALUES
(1,	1,	'Calabria 2022',	'2022-08-04 12:00:00',	'Treno',	'Viaggio in Calabria dell\'estate 2022 facendo tappa a Venezia, Bologna e Roma'),
(2,	1,	'Premantura 2022',	'2022-08-15 12:00:00',	'Macchina',	'Viaggio di andata per il campeggio a Premantura a ferragosto 2022');

INSERT INTO `geopoints` (`id`, `trip_id`, `latitude`, `longitude`, `recorded_at`, `label`) VALUES
(2,	1,	45.817315086704404,	13.48576681068872,	'2022-08-04 05:50:00',	'Stazione Trieste Airport'),
(3,	1,	45.43700838601308,	12.335684577874144,	'2022-08-04 08:30:00',	'Venezia'),
(4,	1,	44.50238248659188,	11.33318206479087,	'2022-08-04 10:30:00',	'Bologna'),
(5,	1,	41.885921309264944,	12.4985174068097,	'2022-08-04 18:30:00',	'Roma'),
(6,	1,	40.851215786677834,	14.195905160482196,	'2022-08-04 20:00:00',	''),
(7,	1,	40.67647235480396,	14.74522152756441,	'2022-08-04 21:30:00',	''),
(8,	1,	40.067562749450005,	15.623343180565398,	'2022-08-05 00:30:00',	''),
(9,	1,	39.121537775852175,	16.085553483612287,	'2022-08-05 02:30:00',	''),
(10,	1,	38.706946223127815,	16.136953987899915,	'2022-08-05 04:20:00',	''),
(11,	1,	38.09565997512077,	15.648062421196729,	'2022-08-05 06:00:00',	'Reggio Calabria'),
(12,	2,	45.65052839048781,	13.776364980619253,	'2022-08-15 05:30:00',	'Trieste'),
(13,	2,	45.54242700317452,	13.72280663306619,	'2022-08-15 07:30:00',	''),
(14,	2,	44.869982881657535,	13.846991389822938,	'2022-08-15 09:50:00',	'Pola'),
(15,	2,	44.8008396264392,	13.910162774460872,	'2022-08-15 11:00:00',	'Premantura');
