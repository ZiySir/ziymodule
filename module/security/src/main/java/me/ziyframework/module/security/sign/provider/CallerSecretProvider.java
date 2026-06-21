package me.ziyframework.module.security.sign.provider;

/** 调用方密钥提供器. */
public interface CallerSecretProvider {

    /** 根据 AK 查询 SK. ak 不存在或已禁用时抛 CallerNotFoundException. */
    String getSk(String ak);
}
