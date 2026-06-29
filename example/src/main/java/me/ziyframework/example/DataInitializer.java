package me.ziyframework.example;

import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import me.ziyframework.module.security.entity.BackendUserDo;
import me.ziyframework.module.security.entity.BackendUserDoRepository;
import me.ziyframework.module.security.entity.OpenCallerDo;
import me.ziyframework.module.security.entity.OpenCallerDoRepository;
import me.ziyframework.module.security.entity.PermissionDo;
import me.ziyframework.module.security.entity.PermissionDoRepository;
import me.ziyframework.module.security.entity.PrincipalRoleDo;
import me.ziyframework.module.security.entity.PrincipalRoleDoRepository;
import me.ziyframework.module.security.entity.PrincipalType;
import me.ziyframework.module.security.entity.RoleDo;
import me.ziyframework.module.security.entity.RoleDoRepository;
import me.ziyframework.module.security.entity.RolePermissionDo;
import me.ziyframework.module.security.entity.RolePermissionDoRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 演示数据初始化.
 * <p>取代原 data.sql,使用 JPA 持久化方式写入,规避原生 SQL 触发 schema 中未知 CHECK 约束.</p>
 * created in 2026-06
 * @author ziy
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final BackendUserDoRepository userRepo;

    private final RoleDoRepository roleRepo;

    private final PermissionDoRepository permissionRepo;

    private final RolePermissionDoRepository rolePermissionRepo;

    private final PrincipalRoleDoRepository principalRoleRepo;

    private final OpenCallerDoRepository openCallerRepo;

    /**
     * 写入演示数据.id 由框架的 {@code @CustomId} 在 save 时自动生成,此处不显式 setId.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Instant now = Instant.now();

        // 用户
        BackendUserDo admin = new BackendUserDo();
        admin.setUid("u-admin");
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setNickName("管理员");
        admin.setDisabled(false);
        admin.setLocked(false);
        admin.setCreatedAt(now);
        admin.setLastUpdatedAt(now);
        userRepo.save(admin);

        BackendUserDo user = new BackendUserDo();
        user.setUid("u-user");
        user.setUsername("user");
        user.setPassword("123456");
        user.setNickName("普通用户");
        user.setDisabled(false);
        user.setLocked(false);
        user.setCreatedAt(now);
        user.setLastUpdatedAt(now);
        userRepo.save(user);

        // 角色 (注意顺序:先建 parent,再建 child,user 继承 guest)
        RoleDo guest = saveRole("guest", "访客", null, 10, now);
        RoleDo adminRole = saveRole("admin", "管理员", null, 100, now);
        RoleDo userRole = saveRole("user", "用户", Objects.requireNonNull(guest.getId()), 50, now);

        // 权限
        PermissionDo pRead = savePermission("user:read", "读用户", now);
        PermissionDo pWrite = savePermission("user:write", "写用户", now);
        PermissionDo pDelete = savePermission("user:delete", "删用户", now);

        // 角色-权限 (admin→全部, user→read)
        saveRolePermission(Objects.requireNonNull(adminRole.getId()), Objects.requireNonNull(pRead.getId()), now);
        saveRolePermission(Objects.requireNonNull(adminRole.getId()), Objects.requireNonNull(pWrite.getId()), now);
        saveRolePermission(Objects.requireNonNull(adminRole.getId()), Objects.requireNonNull(pDelete.getId()), now);
        saveRolePermission(Objects.requireNonNull(userRole.getId()), Objects.requireNonNull(pRead.getId()), now);

        // 主体-角色 (admin→admin, user→user)
        savePrincipalRole(Objects.requireNonNull(admin.getId()), Objects.requireNonNull(adminRole.getId()), now);
        savePrincipalRole(Objects.requireNonNull(user.getId()), Objects.requireNonNull(userRole.getId()), now);

        // 开放平台调用方
        OpenCallerDo caller = new OpenCallerDo();
        caller.setAk("ak-demo-001");
        caller.setSk("sk-demo-001-secret");
        caller.setName("演示调用方");
        caller.setEnabled(true);
        caller.setCreatedAt(now);
        caller.setLastUpdatedAt(now);
        openCallerRepo.save(caller);
    }

    private RoleDo saveRole(String code, String name, @Nullable Long parentId, int sort, Instant now) {
        RoleDo role = new RoleDo();
        role.setCode(code);
        role.setName(name);
        role.setParentId(parentId);
        role.setOwnerType(PrincipalType.BACKEND);
        role.setSort(sort);
        role.setDisabled(false);
        role.setCreatedAt(now);
        role.setBuiltin(false);
        role.setLastUpdatedAt(now);
        return roleRepo.save(role);
    }

    private PermissionDo savePermission(String code, String name, Instant now) {
        PermissionDo permission = new PermissionDo();
        permission.setCode(code);
        permission.setName(name);
        permission.setDisabled(false);
        permission.setCreatedAt(now);
        permission.setBuiltin(false);
        permission.setLastUpdatedAt(now);
        return permissionRepo.save(permission);
    }

    private void saveRolePermission(Long roleId, Long permissionId, Instant now) {
        RolePermissionDo rp = new RolePermissionDo();
        rp.setRoleId(roleId);
        rp.setPermissionId(permissionId);
        rp.setCreatedAt(now);
        rp.setLastUpdatedAt(now);
        rolePermissionRepo.save(rp);
    }

    private void savePrincipalRole(Long principalId, Long roleId, Instant now) {
        PrincipalRoleDo pr = new PrincipalRoleDo();
        pr.setPrincipalType(PrincipalType.BACKEND);
        pr.setPrincipalId(principalId);
        pr.setRoleId(roleId);
        pr.setCreatedAt(now);
        pr.setLastUpdatedAt(now);
        principalRoleRepo.save(pr);
    }
}
