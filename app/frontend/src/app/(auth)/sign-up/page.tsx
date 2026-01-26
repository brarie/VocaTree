'use client';

import { useState } from 'react';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { signUp, resendVerification } from '@/api/user';
import { SignUpRequestSchema } from '@/types/api/user';

const signUpFormSchema = SignUpRequestSchema.extend({
  passwordConfirm: z.string(),
}).refine((data) => data.rawPassword === data.passwordConfirm, {
  message: '비밀번호가 일치하지 않습니다.',
  path: ['passwordConfirm'],
});

type SignUpFormValues = z.infer<typeof signUpFormSchema>;

export default function SignUp() {
  const [step, setStep] = useState<'form' | 'email-sent'>('form');
  const [tempKey, setTempKey] = useState('');
  const [email, setEmail] = useState('');

  const [error, setError] = useState('');
  const [resendLoading, setResendLoading] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);

  const form = useForm<SignUpFormValues>({
    resolver: zodResolver(signUpFormSchema),
    mode: 'onBlur',
    reValidateMode: 'onChange',
    defaultValues: {
      username: '',
      email: '',
      nickname: '',
      rawPassword: '',
      passwordConfirm: '',
    },
  });

  const onSubmit = async (data: SignUpFormValues) => {
    setError('');

    try {
      const result = await signUp({
        username: data.username,
        email: data.email,
        nickname: data.nickname,
        rawPassword: data.rawPassword,
      });

      setTempKey(result.tempKey);
      setEmail(data.email);
      setStep('email-sent');
    } catch (err) {
      setError(err instanceof Error ? err.message : '회원가입에 실패했습니다.');
    }
  };

  const handleResend = async () => {
    if (resendLoading || !tempKey) return;

    setResendLoading(true);
    setResendSuccess(false);
    setError('');

    try {
      await resendVerification({ tempKey });
      setResendSuccess(true);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : '인증 메일 재전송에 실패했습니다.',
      );
    } finally {
      setResendLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: 'easeOut' }}
      >
        <Card className="w-[min(var(--container-md),85vw)] bg-black/90 backdrop-blur-sm glow-border">
          <CardHeader>
            <div className="pt-8 pb-4 flex justify-center">
              <Link href="/" className="group">
                <h1
                  className="text-[min(var(--text-5xl),12vw)] font-black italic text-white group-hover:text-white/80 transition-colors"
                  style={{ fontFamily: 'var(--font-exo-2)' }}
                >
                  MonoVoca
                </h1>
              </Link>
            </div>
          </CardHeader>

          {step === 'form' && (
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)}>
                <CardContent className="space-y-4">
                  <FormField
                    control={form.control}
                    name="username"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-gray-200">아이디</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="영문, 숫자 4~10자"
                            className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage className="text-red-400" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-gray-200">이메일</FormLabel>
                        <FormControl>
                          <Input
                            type="email"
                            placeholder="example@email.com"
                            className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage className="text-red-400" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="nickname"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-gray-200">닉네임</FormLabel>
                        <FormControl>
                          <Input
                            placeholder="영문, 숫자, 한글 2~8자"
                            className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage className="text-red-400" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="rawPassword"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-gray-200">
                          비밀번호
                        </FormLabel>
                        <FormControl>
                          <Input
                            type="password"
                            placeholder="영문, 숫자, 특수문자 8~20자"
                            className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage className="text-red-400" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="passwordConfirm"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-gray-200">
                          비밀번호 확인
                        </FormLabel>
                        <FormControl>
                          <Input
                            type="password"
                            placeholder="비밀번호를 다시 입력하세요"
                            className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage className="text-red-400" />
                      </FormItem>
                    )}
                  />

                  {error && <p className="text-sm text-red-400">{error}</p>}

                  <Button
                    type="submit"
                    className="w-full bg-white text-black hover:bg-gray-200 font-semibold"
                    size="lg"
                    disabled={form.formState.isSubmitting}
                  >
                    {form.formState.isSubmitting ? '처리 중...' : '회원가입'}
                  </Button>
                </CardContent>

                <CardFooter className="flex flex-col space-y-4 pt-4">
                  <div className="flex items-center justify-center text-sm">
                    <span className="text-gray-400">
                      이미 계정이 있으신가요?
                    </span>
                    <Link
                      href="/login"
                      className="text-white hover:text-gray-300 font-medium transition-colors ml-2"
                    >
                      로그인
                    </Link>
                  </div>
                </CardFooter>
              </form>
            </Form>
          )}

          {step === 'email-sent' && (
            <>
              <CardContent className="space-y-4 text-center py-8 -mt-16">
                <div className="space-y-4">
                  <div className="text-6xl">📧</div>
                  <h2 className="text-2xl font-bold text-white">
                    이메일을 확인해주세요
                  </h2>
                  <p className="text-gray-300">
                    <span className="font-semibold text-white">{email}</span>로
                    <br />
                    인증 링크를 보냈습니다.
                  </p>
                  <p className="text-sm text-gray-400">
                    이메일의 링크를 클릭하여 회원가입을 완료해주세요.
                    <br />
                    인증이 완료되었다면 이 페이지를 닫으셔도 됩니다.
                  </p>

                  {resendSuccess && (
                    <p className="text-sm text-green-400">
                      인증 메일이 재전송되었습니다.
                    </p>
                  )}

                  {error && <p className="text-sm text-red-400">{error}</p>}

                  <div className="pt-4 space-y-2 -mb-8">
                    <Button
                      type="button"
                      variant="outline"
                      onClick={handleResend}
                      disabled={resendLoading}
                      className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                    >
                      {resendLoading ? '전송 중...' : '인증 메일 다시 보내기'}
                    </Button>

                    <Button
                      asChild
                      variant="outline"
                      className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                    >
                      <Link href="/login">로그인 페이지로 이동</Link>
                    </Button>
                  </div>
                </div>
              </CardContent>
            </>
          )}
        </Card>
      </motion.div>
    </main>
  );
}
