import { defineStore } from 'pinia';

export const useUiStore = defineStore('ui', {
    state: () => ({
        isAdminPage: false, // 是否处于管理员页面
    }),
    actions: {
        setIsAdminPage(status) {
            this.isAdminPage = status;
        },
    },
});
