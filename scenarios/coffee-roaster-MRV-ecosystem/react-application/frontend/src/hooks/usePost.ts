import { useState } from 'react';
import axios from 'axios';

const usePost = () => {
    const [data, setData] = useState<any>(null);
    const [error, setError] = useState<any>(null);
    const [loading, setLoading] = useState<any>(false);

    const post = async (url: any, body: any) => {
        setLoading(true);
        setError(null);
        try {
            const response = await axios.post(url, body);
            setData(response.data);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    return { data, error, loading, post };
};

export default usePost;
