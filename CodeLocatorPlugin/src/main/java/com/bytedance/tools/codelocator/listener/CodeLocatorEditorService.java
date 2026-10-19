package com.bytedance.tools.codelocator.listener;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;

/**
 * 管理 CodeLocator 全局编辑器监听器的生命周期.
 */
@Service(Service.Level.APP)
public final class CodeLocatorEditorService implements Disposable {

    @Override
    public void dispose() {
        // 监听器作为子资源由平台 Disposer 自动释放.
    }
}
