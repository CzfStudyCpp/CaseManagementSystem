import { defineStore } from "pinia";

export const useDemandStore = defineStore("demand", {
    state: () => ({
        demands: [], // 存储所有推荐需求数据
        selectedDemand: null, // 当前选中的需求
    }),

    actions: {
        setDemands(demands) {
            this.demands = demands; // 设置推荐需求列表
        },
        setSelectedDemand(demand) {
            this.selectedDemand = demand; // 设置当前选中的需求
        },
    },
});