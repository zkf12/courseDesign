import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const usePersistStore = defineStore('token',()=>{
    const token = ref('')
    const userInfo = ref(null);
    const DEFAULT_AVATAR = 'userPicture/nopic.png' // 定义默认头像文件名

    // Getter：计算头像完整 URL
    const userAvatarUrl = computed(() => {
        const basePath = '/api/' // 注意开头的斜杠
        return userInfo.value?.picture 
            ? `${basePath}${userInfo.value.picture}`
            : `${basePath}${DEFAULT_AVATAR}`
    })

    const setToken = (newToken)=>{
        token.value = newToken
    }

    const removeToken = ()=>{
        token.value=''
    }

    const setUserInfo = (newInfo)=>{
        userInfo.value = newInfo
    }

    const removeUserInfo = ()=>{
        userInfo.value = null
    }

    return {
        token, setToken, removeToken, userAvatarUrl, userInfo, setUserInfo, removeUserInfo
    }
},{
    persist: true // 启用持久化
})