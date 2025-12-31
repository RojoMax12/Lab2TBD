import api from './api.js';

export default {
    getDailyWeights() {
        return api.get('/api/reports/daily-weight');
    }
};
