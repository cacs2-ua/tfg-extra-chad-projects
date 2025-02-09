DELETE FROM medical_reports;
DELETE FROM users;


INSERT INTO public.users (id, date_of_birth, email, password, username) VALUES (1, '2025-02-06', 'user1@gmail.com', '$2a$10$4OkkvNCpZEIGga6RiVdgfe918FMP78h3YLT8vR6i2uHEUJYtMrX2u', 'user1');
INSERT INTO public.users (id, date_of_birth, email, password, username) VALUES (2, '2025-01-29', 'user2@gmail.com', '$2a$10$JA27RTdJE3mUPSwb0lZkTeQHmCFWnNvbY7i0eDed7R6iqHBhSTa1m', 'user2');


INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (1, 'application/pdf', 'documento-cofirmado (6).pdf', 'informes/user-1/documento-cofirmado (6).pdf', 111106, '2025-02-09 12:26:24.247000', 1);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (2, 'application/pdf', 'Instalacio¿n de Go (1).pdf', 'informes/user-1/Instalacio¿n de Go (1).pdf', 92309, '2025-02-09 14:47:24.013000', 1);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (3, 'application/pdf', 'wcms_825183.pdf', 'informes/user-1/wcms_825183.pdf', 174447, '2025-02-09 14:47:29.806000', 1);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (4, 'application/pdf', 'documento-firmado (6).pdf', 'informes/user-5/documento-firmado (6).pdf', 55701, '2025-02-09 14:48:30.946000', 2);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (5, 'application/pdf', 'documento-cofirmado (6) (1).pdf', 'informes/user-5/documento-cofirmado (6) (1).pdf', 111106, '2025-02-09 14:48:47.965000', 2);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (6, 'application/pdf', 'documento-cofirmado (6).pdf', 'informes/user-5/documento-cofirmado (6).pdf', 111106, '2025-02-09 14:48:48.275000', 2);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (7, 'image/png', 'union-humana.png', 'informes/user-5/union-humana.png', 57790, '2025-02-09 14:48:53.392000', 2);
INSERT INTO public.medical_reports (id, file_type, name, s3_key, size, uploaded_at, user_id) VALUES (8, 'image/png', 'FUERZA.png', 'informes/user-5/FUERZA.png', 32366, '2025-02-09 14:49:22.805000', 2);


SELECT setval('public.medical_reports_id_seq', COALESCE((SELECT MAX(id) FROM public.medical_reports), 0) + 1, false);
SELECT setval('public.users_id_seq', COALESCE((SELECT MAX(id) FROM public.users), 0) + 1, false);
