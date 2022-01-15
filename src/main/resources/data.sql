INSERT INTO `category` (`id`, `cat_description`, `cat_name`, `category_error`) VALUES
(1, 'This category is for system issues and solving those problems', 'Solve problem for system', NULL),
(2, 'Solve problem for infrastructure technical', 'Solve problem for infrastructure technical', NULL),
(3, 'Solve problem for Other techniques', 'Solve problem for Other techniques', NULL);


INSERT INTO `sub_category` (`id`, `cat_name`, `category_id`, `sub_cat_err`) VALUES
(1, 'Non application', 1, NULL),
(2, 'Application', 1, NULL),
(3, 'Random', 1, NULL),
(4, 'Network', 2, NULL),
(5, 'Computer and Printer', 2, NULL),
(6, 'Telephone', 2, NULL);

INSERT INTO `user` (`_id`, `access_token`, `address`, `created_at`, `date_of_birth`, `email`, `first_name`, `gender`, `last_name`, `name_of_unit`, `password`, `phone_number`, `place_of_birth`, `position`, `updated_at`, `login_error`) VALUES
(1, NULL, NULL, NULL, NULL, 'dan@g.com', 'Daniel', 1, 'Isaac', NULL, 'password', NULL, NULL, 1, NULL, NULL);