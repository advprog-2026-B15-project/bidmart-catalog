import { AuctionItem } from '@/types';

const API_BASE_URLS = {
  catalog: process.env.NEXT_PUBLIC_CATALOG_URL || 'http://98.93.134.192:8082',
  auth: process.env.NEXT_PUBLIC_AUTH_URL || 'http://98.93.134.192:8081',
  auction: process.env.NEXT_PUBLIC_AUCTION_URL || 'http://98.93.134.192:8083',
  wallet: process.env.NEXT_PUBLIC_WALLET_URL || 'http://98.93.134.192:8084',
  booking: process.env.NEXT_PUBLIC_BOOKING_URL || 'http://98.93.134.192:8085',
};

type Service = keyof typeof API_BASE_URLS;

async function apiFetch<T>(service: Service, endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${API_BASE_URLS[service]}${endpoint}`;
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'An error occurred' }));
    throw new Error(error.message || response.statusText);
  }

  return response.json();
}

export const catalogApi = {
  getListings: (page = 0, size = 10) => 
    apiFetch<{ content: any[], totalPages: number, totalElements: number }>('catalog', `/api/listings?page=${page}&size=${size}`),
  getListing: (id: string) => 
    apiFetch<any>('catalog', `/api/listings/${id}`),
  getCategories: () =>
    apiFetch<any[]>('catalog', '/api/categories'),
  createListing: (data: any, sellerId: string, role: string) =>
    apiFetch<any>('catalog', '/api/listings', {
      method: 'POST',
      headers: {
        'X-User-Id': sellerId,
        'X-User-Role': role,
      },
      body: JSON.stringify(data),
    }),
};

export const authApi = {
  login: (data: any) => 
    apiFetch<any>('auth', '/api/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  getMe: (token: string) => 
    apiFetch<any>('auth', '/api/users/me', { headers: { Authorization: `Bearer ${token}` } }),
};

export const auctionApi = {
  getAuctions: () => 
    apiFetch<any[]>('auction', '/api/auctions'),
  getAuction: (id: string) => 
    apiFetch<any>('auction', `/api/auctions/${id}`),
  placeBid: (id: string, amount: number, userId: string) => 
    apiFetch<any>('auction', `/api/auctions/${id}/bids`, {
      method: 'POST',
      headers: { 'X-User-Id': userId },
      body: JSON.stringify({ amount }),
    }),
};

export const walletApi = {
  getWallet: (userId: string) => 
    apiFetch<any>('wallet', `/api/wallet/${userId}`, { headers: { 'X-User-Id': userId } }),
};

export const bookingApi = {
  getBookings: (userId: string) => 
    apiFetch<any[]>('booking', '/api/bookings/me', { headers: { 'X-User-Id': userId } }),
  getNotifications: (userId: string) => 
    apiFetch<any[]>('booking', '/api/notifications/me', { headers: { 'X-User-Id': userId } }),
};
