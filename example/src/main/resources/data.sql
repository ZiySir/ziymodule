-- 用户 (id=1, admin)
INSERT INTO backend_user (id, uid, username, password, nick_name, created_at, last_updated_at)
VALUES (1, 'u-admin', 'admin', '123456', '管理员', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 用户 (id=2, 普通用户, 用于演示角色继承)
INSERT INTO backend_user (id, uid, username, password, nick_name, created_at, last_updated_at)
VALUES (2, 'u-user', 'user', '123456', '普通用户', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 角色 (id=1 admin, id=2 user, id=3 guest; user 继承自 guest)
INSERT INTO role (id, code, name, parent_id, owner_type, sort, disabled, created_at, last_updated_at)
VALUES (1, 'admin', '管理员', NULL, 1, 100, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO role (id, code, name, parent_id, owner_type, sort, disabled, created_at, last_updated_at)
VALUES (2, 'user', '用户', 3, 1, 50, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO role (id, code, name, parent_id, owner_type, sort, disabled, created_at, last_updated_at)
VALUES (3, 'guest', '访客', NULL, 1, 10, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 权限 (id=1~3)
INSERT INTO permission (id, code, name, disabled, created_at, last_updated_at)
VALUES (1, 'user:read', '读用户', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO permission (id, code, name, disabled, created_at, last_updated_at)
VALUES (2, 'user:write', '写用户', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO permission (id, code, name, disabled, created_at, last_updated_at)
VALUES (3, 'user:delete', '删用户', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 角色-权限 (admin→全部, user→read)
INSERT INTO role_permission (id, role_id, permission_id, created_at, last_updated_at) VALUES (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO role_permission (id, role_id, permission_id, created_at, last_updated_at) VALUES (2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO role_permission (id, role_id, permission_id, created_at, last_updated_at) VALUES (3, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO role_permission (id, role_id, permission_id, created_at, last_updated_at) VALUES (4, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 主体-角色 (admin→admin, user→user)
INSERT INTO principal_role (id, principal_type, principal_id, role_id, created_at, last_updated_at)
VALUES (1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO principal_role (id, principal_type, principal_id, role_id, created_at, last_updated_at)
VALUES (2, 1, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 开放平台调用方
INSERT INTO open_caller (id, ak, sk, name, enabled, created_at, last_updated_at)
VALUES (1, 'ak-demo-001', 'sk-demo-001-secret', '演示调用方', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
