INSERT INTO public."permission" (http_method,permission_key,url_pattern) VALUES
	 ('GET','USER_VIEW','/api/auth/users/**'),
	 ('GET','UI_ROUTES','/api/auth/ui-routes'),
	 ('PUT','USER_EDIT','/api/auth/users/**'),
	 ('DELETE','USER_DELETE','/api/auth/users/**'),
	 ('GET','USER_VIEW','/api/auth/tareas/**');
